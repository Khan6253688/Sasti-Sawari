/**
 * Cloud Functions for Sasti Sawari
 * Manages 30-day subscription cycles, payment verification statuses, and automatic access locking.
 */

const functions = require("firebase-functions");
const admin = require("firebase-admin");

admin.initializeApp();
const db = admin.firestore();

const THIRTY_DAYS_MS = 30 * 24 * 60 * 60 * 1000;
const SUBSCRIPTION_FEE_PKR = 100.0;

/**
 * 1. Automatic Access Locking & Cycle Checker
 * Scheduled cron function running every 12 hours.
 * Identifies expired active subscriptions and locks access.
 */
exports.checkSubscriptionCycles = functions.pubsub
  .schedule("every 12 hours")
  .onRun(async (context) => {
    const now = admin.firestore.Timestamp.now();
    const nowMillis = now.toMillis();
    console.log(`Checking subscription cycles at ${new Date(nowMillis).toISOString()}`);

    const activeRidersSnapshot = await db
      .collection("riders")
      .where("subscriptionStatus", "==", "ACTIVE")
      .get();

    let lockedCount = 0;
    const batch = db.batch();

    activeRidersSnapshot.forEach((doc) => {
      const data = doc.data();
      const expiresAt = data.expiresAt;

      let isExpired = false;
      if (!expiresAt) {
        isExpired = true;
      } else if (expiresAt.toMillis && expiresAt.toMillis() < nowMillis) {
        isExpired = true;
      }

      if (isExpired) {
        batch.update(doc.ref, {
          subscriptionStatus: "EXPIRED",
          isOnline: false,
          lockedAt: now,
          updatedAt: now,
          lockReason: "30-day subscription period ended. Renewal required for Rs. 100."
        });
        lockedCount++;
      }
    });

    if (lockedCount > 0) {
      await batch.commit();
      console.log(`Successfully locked ${lockedCount} expired rider accounts.`);
    } else {
      console.log("No expired riders found. All active accounts in good standing.");
    }

    return null;
  });

/**
 * 2. On Payment Status Changed Trigger
 * When an admin marks a payment "APPROVED" in the dashboard,
 * automatically activate the rider for 30 days and increment system revenue.
 */
exports.onPaymentStatusChanged = functions.firestore
  .document("payments/{paymentId}")
  .onUpdate(async (change, context) => {
    const beforeData = change.before.data();
    const afterData = change.after.data();

    // Trigger only on transition to APPROVED
    if (beforeData.status !== "APPROVED" && afterData.status === "APPROVED") {
      const riderId = afterData.riderId;
      const now = admin.firestore.Timestamp.now();
      const nowMillis = now.toMillis();

      const riderRef = db.collection("riders").doc(riderId);
      const riderDoc = await riderRef.get();

      if (!riderDoc.exists) {
        console.error(`Rider ${riderId} not found for payment approval.`);
        return;
      }

      const riderData = riderDoc.data();
      let currentExpiryMillis = nowMillis;

      if (riderData.expiresAt && riderData.expiresAt.toMillis) {
        const existingExpiry = riderData.expiresAt.toMillis();
        // If rider still has active days left, add 30 days from existing expiry
        if (existingExpiry > nowMillis) {
          currentExpiryMillis = existingExpiry;
        }
      }

      const newExpiryMillis = currentExpiryMillis + THIRTY_DAYS_MS;
      const newExpiryTimestamp = admin.firestore.Timestamp.fromMillis(newExpiryMillis);

      // Activate Rider
      await riderRef.update({
        subscriptionStatus: "ACTIVE",
        activatedAt: now,
        expiresAt: newExpiryTimestamp,
        lastPaymentTxnId: afterData.transactionId || "",
        lastPaymentMethod: afterData.paymentMethod || "JazzCash",
        updatedAt: now,
        isOnline: true
      });

      // Update System Revenue in Admin Config
      const adminConfigRef = db.collection("system_config").doc("admin_settings");
      await db.runTransaction(async (transaction) => {
        const configDoc = await transaction.get(adminConfigRef);
        const currentRevenue = configDoc.exists ? (configDoc.data().totalRevenueCollected || 0) : 0;
        transaction.set(
          adminConfigRef,
          {
            totalRevenueCollected: currentRevenue + (afterData.amount || SUBSCRIPTION_FEE_PKR),
            updatedAt: now
          },
          { merge: true }
        );
      });

      console.log(`Activated rider ${riderId} until ${new Date(newExpiryMillis).toISOString()}`);
    }
  });

/**
 * 3. Callable HTTP Function: Approve Payment Renewal
 * Called securely by the Admin Dashboard.
 */
exports.approveRiderPayment = functions.https.onCall(async (data, context) => {
  const { paymentId, riderId, adminPin } = data;

  // Verify PIN or Admin claim
  const configDoc = await db.collection("system_config").doc("admin_settings").get();
  const validPin = configDoc.exists ? configDoc.data().adminPin || "1234" : "1234";

  if (adminPin !== validPin) {
    throw new functions.https.HttpsError(
      "permission-denied",
      "Invalid Admin PIN for payment approval."
    );
  }

  const paymentRef = db.collection("payments").doc(paymentId);
  const now = admin.firestore.Timestamp.now();

  await paymentRef.update({
    status: "APPROVED",
    reviewedAt: now,
    reviewedBy: "Admin",
    updatedAt: now
  });

  return { success: true, message: `Payment ${paymentId} approved and rider ${riderId} activated for 30 days.` };
});

/**
 * 4. Callable HTTP Function: Trigger Manual Subscription Lock (for testing/maintenance)
 */
exports.manualCycleCheck = functions.https.onRequest(async (req, res) => {
  const now = admin.firestore.Timestamp.now();
  const nowMillis = now.toMillis();

  const activeRidersSnapshot = await db
    .collection("riders")
    .where("subscriptionStatus", "==", "ACTIVE")
    .get();

  let locked = 0;
  const batch = db.batch();

  activeRidersSnapshot.forEach((doc) => {
    const data = doc.data();
    if (!data.expiresAt || (data.expiresAt.toMillis && data.expiresAt.toMillis() < nowMillis)) {
      batch.update(doc.ref, {
        subscriptionStatus: "EXPIRED",
        isOnline: false,
        updatedAt: now
      });
      locked++;
    }
  });

  if (locked > 0) {
    await batch.commit();
  }

  res.json({ success: true, lockedCount: locked, checkedAt: new Date(nowMillis).toISOString() });
});
