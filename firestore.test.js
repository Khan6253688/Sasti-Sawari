const { initializeTestEnvironment, assertFails, assertSucceeds } = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;

before(async () => {
  const rules = fs.readFileSync("firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: "gen-lang-client-0449480898",
    firestore: {
      rules,
      host: "127.0.0.1",
      port: 8085
    }
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

test("unauthenticated users cannot create rider profile", async () => {
  const unauthedDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(
    unauthedDb.collection("riders").doc("SW-1001").set({
      userId: "user_123",
      riderId: "SW-1001",
      name: "Tariq",
      phone: "03001234567",
      cityId: "multan",
      subscriptionStatus: "ACTIVE"
    })
  );
});

test("authenticated user can create their own rider profile with valid data", async () => {
  const authedDb = testEnv.authenticatedContext("user_123").firestore();
  await assertSucceeds(
    authedDb.collection("riders").doc("SW-1001").set({
      userId: "user_123",
      riderId: "SW-1001",
      name: "Muhammad Rashid",
      phone: "03085142950",
      cityId: "multan",
      subscriptionStatus: "ACTIVE"
    })
  );
});

test("authenticated user can submit payment verification", async () => {
  const authedDb = testEnv.authenticatedContext("user_123").firestore();
  await assertSucceeds(
    authedDb.collection("payments").doc("PAY-101").set({
      paymentId: "PAY-101",
      riderId: "SW-1001",
      userId: "user_123",
      amount: 100.0,
      transactionId: "TXN-8849102",
      paymentMethod: "JazzCash",
      status: "PENDING"
    })
  );
});
