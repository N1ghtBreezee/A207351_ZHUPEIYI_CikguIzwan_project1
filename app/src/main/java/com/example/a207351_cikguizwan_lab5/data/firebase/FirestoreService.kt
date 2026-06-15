package com.example.a207351_cikguizwan_lab5.data.firebase

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.tasks.await

data class CommunityWorkout(
    val id: String = "",
    val userName: String = "",
    val workoutType: String = "",
    val durationMinutes: Int = 0,
    val caloriesBurned: Int = 0,
    val timestamp: Long = System.currentTimeMillis()
)

object FirestoreService {
    private val db = FirebaseFirestore.getInstance()
    private const val COLLECTION = "community_workouts"

    suspend fun addWorkout(workout: CommunityWorkout): Result<String> {
        return try {
            val docRef = db.collection(COLLECTION).document()
            val workoutWithId = workout.copy(id = docRef.id)
            docRef.set(workoutWithId).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAllWorkouts(): Result<List<CommunityWorkout>> {
        return try {
            val snapshot = db.collection(COLLECTION)
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .limit(50)
                .get()
                .await()
            val workouts = snapshot.documents.mapNotNull { it.toObject<CommunityWorkout>() }
            Result.success(workouts)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}