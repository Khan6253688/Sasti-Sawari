package com.example.data.repository

import android.content.Context
import android.util.Log
import com.example.R
import com.example.data.model.FirestorePayment
import com.example.data.model.FirestoreRider
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class FirestoreSubscriptionRepository(private val context: Context) {

  private val TAG = "FirestoreSubRepo"

  // Lazy safe initialization of named Firestore instance
  val firestore: FirebaseFirestore? by lazy {
    try {
      FirebaseApp.initializeApp(context)
      val dbId = context.getString(R.string.firestore_database_id)
      Log.d(TAG, "Connecting to named Firestore: $dbId")
      FirebaseFirestore.getInstance(dbId)
    } catch (e: Exception) {
      Log.e(TAG, "Failed to initialize Firestore with named database", e)
      null
    }
  }

  // Observe Pending Payments in real-time
  fun observePendingPayments(): Flow<List<FirestorePayment>> = callbackFlow {
    val db = firestore
    if (db == null) {
      trySend(emptyList())
      awaitClose { }
      return@callbackFlow
    }

    var registration: ListenerRegistration? = null
    try {
      registration = db.collection("payments")
        .whereEqualTo("status", "PENDING")
        .addSnapshotListener { snapshot, error ->
          if (error != null) {
            Log.w(TAG, "Error listening to pending payments: ${error.message}")
            return@addSnapshotListener
          }
          if (snapshot != null) {
            val payments = snapshot.documents.mapNotNull { doc ->
              try {
                doc.toObject(FirestorePayment::class.java)?.copy(
                  id = doc.id,
                  paymentId = if (doc.getString("paymentId").isNullOrBlank()) doc.id else doc.getString("paymentId")!!
                )
              } catch (e: Exception) {
                Log.w(TAG, "Error deserializing payment ${doc.id}", e)
                null
              }
            }.sortedByDescending { it.submittedAt }
            trySend(payments)
          }
        }
    } catch (e: Exception) {
      Log.e(TAG, "Failed to register payments listener", e)
      trySend(emptyList())
    }

    awaitClose {
      registration?.remove()
    }
  }

  // Observe All Payments in real-time (Pending, Approved, Rejected)
  fun observeAllPayments(): Flow<List<FirestorePayment>> = callbackFlow {
    val db = firestore
    if (db == null) {
      trySend(emptyList())
      awaitClose { }
      return@callbackFlow
    }

    var registration: ListenerRegistration? = null
    try {
      registration = db.collection("payments")
        .addSnapshotListener { snapshot, error ->
          if (error != null) {
            Log.w(TAG, "Error listening to all payments: ${error.message}")
            return@addSnapshotListener
          }
          if (snapshot != null) {
            val payments = snapshot.documents.mapNotNull { doc ->
              try {
                doc.toObject(FirestorePayment::class.java)?.copy(
                  id = doc.id,
                  paymentId = if (doc.getString("paymentId").isNullOrBlank()) doc.id else doc.getString("paymentId")!!
                )
              } catch (e: Exception) {
                Log.w(TAG, "Error deserializing payment ${doc.id}", e)
                null
              }
            }.sortedByDescending { it.submittedAt }
            trySend(payments)
          }
        }
    } catch (e: Exception) {
      Log.e(TAG, "Failed to register all payments listener", e)
      trySend(emptyList())
    }

    awaitClose {
      registration?.remove()
    }
  }

  // Observe All Registered Riders in real-time
  fun observeAllRiders(): Flow<List<FirestoreRider>> = callbackFlow {
    val db = firestore
    if (db == null) {
      trySend(emptyList())
      awaitClose { }
      return@callbackFlow
    }

    var registration: ListenerRegistration? = null
    try {
      registration = db.collection("riders")
        .addSnapshotListener { snapshot, error ->
          if (error != null) {
            Log.w(TAG, "Error listening to riders: ${error.message}")
            return@addSnapshotListener
          }
          if (snapshot != null) {
            val riders = snapshot.documents.mapNotNull { doc ->
              try {
                doc.toObject(FirestoreRider::class.java)?.copy(
                  id = doc.id,
                  riderId = if (doc.getString("riderId").isNullOrBlank()) doc.id else doc.getString("riderId")!!
                )
              } catch (e: Exception) {
                Log.w(TAG, "Error deserializing rider ${doc.id}", e)
                null
              }
            }.sortedBy { it.name }
            trySend(riders)
          }
        }
    } catch (e: Exception) {
      Log.e(TAG, "Failed to register riders listener", e)
      trySend(emptyList())
    }

    awaitClose {
      registration?.remove()
    }
  }

  // Approve payment: updates payment to APPROVED and extends rider by 30 days
  suspend fun approvePayment(
    paymentId: String,
    riderId: String,
    durationDays: Int = 30
  ): Boolean = suspendCancellableCoroutine { continuation ->
    val db = firestore
    if (db == null) {
      continuation.resume(false)
      return@suspendCancellableCoroutine
    }

    val now = System.currentTimeMillis()
    val paymentDocRef = db.collection("payments").document(paymentId)
    val riderDocRef = db.collection("riders").document(riderId)

    db.runTransaction { transaction ->
      val riderSnap = transaction.get(riderDocRef)
      val currentExpiry = riderSnap.getLong("expiresAt") ?: 0L
      val currentStatus = riderSnap.getString("subscriptionStatus") ?: "EXPIRED"

      val baseTime = if (currentStatus == "ACTIVE" && currentExpiry > now) {
        currentExpiry
      } else {
        now
      }
      val newExpiry = baseTime + (durationDays.toLong() * 24 * 60 * 60 * 1000L)

      // 1. Update Payment status
      transaction.update(
        paymentDocRef,
        mapOf(
          "status" to "APPROVED",
          "reviewedAt" to now,
          "notes" to "Approved by Admin for 30 days access"
        )
      )

      // 2. Update Rider subscription
      if (riderSnap.exists()) {
        transaction.update(
          riderDocRef,
          mapOf(
            "subscriptionStatus" to "ACTIVE",
            "activatedAt" to now,
            "expiresAt" to newExpiry,
            "isOnline" to true
          )
        )
      }
      null
    }.addOnSuccessListener {
      Log.d(TAG, "Payment $paymentId approved for rider $riderId")
      continuation.resume(true)
    }.addOnFailureListener { error ->
      Log.e(TAG, "Failed to approve payment $paymentId", error)
      continuation.resume(false)
    }
  }

  // Reject payment: updates payment to REJECTED and sets rider to EXPIRED
  suspend fun rejectPayment(
    paymentId: String,
    riderId: String,
    reason: String = ""
  ): Boolean = suspendCancellableCoroutine { continuation ->
    val db = firestore
    if (db == null) {
      continuation.resume(false)
      return@suspendCancellableCoroutine
    }

    val now = System.currentTimeMillis()
    val paymentDocRef = db.collection("payments").document(paymentId)
    val riderDocRef = db.collection("riders").document(riderId)

    db.runTransaction { transaction ->
      transaction.update(
        paymentDocRef,
        mapOf(
          "status" to "REJECTED",
          "reviewedAt" to now,
          "notes" to if (reason.isBlank()) "Rejected by Admin" else reason
        )
      )
      val riderSnap = transaction.get(riderDocRef)
      if (riderSnap.exists()) {
        transaction.update(
          riderDocRef,
          mapOf(
            "subscriptionStatus" to "EXPIRED"
          )
        )
      }
      null
    }.addOnSuccessListener {
      Log.d(TAG, "Payment $paymentId rejected")
      continuation.resume(true)
    }.addOnFailureListener { error ->
      Log.e(TAG, "Failed to reject payment $paymentId", error)
      continuation.resume(false)
    }
  }

  // Direct 30-day renewal for a rider
  suspend fun renewRiderSubscription(riderId: String, durationDays: Int = 30): Boolean =
    suspendCancellableCoroutine { continuation ->
      val db = firestore
      if (db == null) {
        continuation.resume(false)
        return@suspendCancellableCoroutine
      }

      val now = System.currentTimeMillis()
      val riderDocRef = db.collection("riders").document(riderId)

      db.runTransaction { transaction ->
        val riderSnap = transaction.get(riderDocRef)
        val currentExpiry = riderSnap.getLong("expiresAt") ?: 0L
        val currentStatus = riderSnap.getString("subscriptionStatus") ?: "EXPIRED"

        val baseTime = if (currentStatus == "ACTIVE" && currentExpiry > now) {
          currentExpiry
        } else {
          now
        }
        val newExpiry = baseTime + (durationDays.toLong() * 24 * 60 * 60 * 1000L)

        transaction.update(
          riderDocRef,
          mapOf(
            "subscriptionStatus" to "ACTIVE",
            "activatedAt" to now,
            "expiresAt" to newExpiry,
            "isOnline" to true
          )
        )
        null
      }.addOnSuccessListener {
        Log.d(TAG, "Rider $riderId renewed for $durationDays days")
        continuation.resume(true)
      }.addOnFailureListener { error ->
        Log.e(TAG, "Failed to renew rider $riderId", error)
        continuation.resume(false)
      }
    }

  // Lock / Suspend rider
  suspend fun lockRider(riderId: String): Boolean = suspendCancellableCoroutine { continuation ->
    val db = firestore
    if (db == null) {
      continuation.resume(false)
      return@suspendCancellableCoroutine
    }

    db.collection("riders").document(riderId)
      .update(
        mapOf(
          "subscriptionStatus" to "EXPIRED",
          "isOnline" to false
        )
      )
      .addOnSuccessListener { continuation.resume(true) }
      .addOnFailureListener { continuation.resume(false) }
  }

  // Submit payment from rider
  suspend fun submitPayment(payment: FirestorePayment): Boolean = suspendCancellableCoroutine { continuation ->
    val db = firestore
    if (db == null) {
      continuation.resume(false)
      return@suspendCancellableCoroutine
    }

    val id = if (payment.paymentId.isNotBlank()) payment.paymentId else "PAY-${System.currentTimeMillis()}"
    val paymentWithId = payment.copy(paymentId = id, id = id)

    db.collection("payments").document(id)
      .set(paymentWithId, SetOptions.merge())
      .addOnSuccessListener {
        // Also update rider pending status
        db.collection("riders").document(payment.riderId)
          .set(
            mapOf(
              "subscriptionStatus" to "PENDING",
              "lastPaymentTxnId" to payment.transactionId,
              "lastPaymentMethod" to payment.paymentMethod
            ),
            SetOptions.merge()
          )
        continuation.resume(true)
      }
      .addOnFailureListener {
        continuation.resume(false)
      }
  }

  // No dummy data seeded: collections are populated exclusively by genuine registered riders and real payments
  suspend fun seedInitialDataIfEmpty() {
    // Clean initial state
  }
}
