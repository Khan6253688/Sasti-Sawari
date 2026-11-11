package com.example.data.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class FirestorePayment(
  @DocumentId val id: String = "",
  val paymentId: String = "",
  val riderId: String = "",
  val userId: String = "",
  val riderName: String = "",
  val riderEmail: String = "",
  val riderPhone: String = "",
  val riderCity: String = "multan",
  val riderStand: String = "Ghanta Ghar Stand",
  val amount: Double = 100.0,
  val transactionId: String = "",
  val paymentMethod: String = "JazzCash", // JazzCash, Easypaisa, Bank
  val proofUri: String = "",
  val status: String = "PENDING", // PENDING, APPROVED, REJECTED
  val submittedAt: Long = System.currentTimeMillis(),
  val reviewedAt: Long? = null,
  val notes: String = ""
)

@IgnoreExtraProperties
data class FirestoreRider(
  @DocumentId val id: String = "",
  val riderId: String = "",
  val userId: String = "",
  val name: String = "",
  val email: String = "",
  val phone: String = "",
  val cityId: String = "multan",
  val bikeModel: String = "Honda CD 70",
  val plate: String = "MNK-4821",
  val standName: String = "Ghanta Ghar Stand",
  val standAddress: String = "Ghanta Ghar, Multan",
  val photoUri: String = "",
  val tagline: String = "Direct Stand Pickup • Safe Ride",
  val subscriptionStatus: String = "ACTIVE", // ACTIVE, PENDING, EXPIRED, SUSPENDED
  val subscriptionPrice: Double = 100.0,
  val activatedAt: Long = System.currentTimeMillis(),
  val expiresAt: Long = System.currentTimeMillis() + (30L * 24 * 60 * 60 * 1000),
  val lastPaymentTxnId: String = "",
  val lastPaymentMethod: String = "JazzCash",
  val isOnline: Boolean = true
)
