package com.carbondev.tallynote.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.carbondev.tallynote.datamodel.STORE_PRODUCT
import com.carbondev.tallynote.datamodel.STORE_PRODUCT_MODIFIED
import com.carbondev.tallynote.datamodel.StoreProduct
import com.carbondev.tallynote.datamodel.StoreProductModified
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.flow.callbackFlow

object FirebaseStoreProductRepository {

    private val mAllStoreProduct = MutableLiveData<ArrayList<StoreProduct>>()
    val allStoreProduct: LiveData<ArrayList<StoreProduct>> = mAllStoreProduct

    private val mSingleStoreProduct = MutableLiveData<StoreProduct>()
    val singleStoreProduct: LiveData<StoreProduct> = mSingleStoreProduct

    private val mAllStoreProductModified = MutableLiveData<ArrayList<StoreProductModified>>()
    val allStoreProductModified: LiveData<ArrayList<StoreProductModified>> =
        mAllStoreProductModified

    private val mSingleStoreProductModified = MutableLiveData<StoreProductModified>()
    val singleStoreProductModified: LiveData<StoreProductModified> =
        mSingleStoreProductModified

    // store product

    fun saveStoreProduct(storeProduct: StoreProduct) {
        val userRef = FirebaseDataRepository.userRef()
        if (userRef != null) {

            val newKey = userRef.child(STORE_PRODUCT).push().key

            if (newKey != null) {
                storeProduct.key = newKey
                storeProduct.lastEdited = System.currentTimeMillis()
                userRef.child(STORE_PRODUCT).child(newKey).setValue(storeProduct)

//                fetchSingleNote(newKey)
            }

        }

    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun saveStoreProductFlow(storeProduct: StoreProduct) = callbackFlow<Result<StoreProduct>>  {
        val userRef = FirebaseDataRepository.userRef()
        if (userRef != null) {

            val newKey = userRef.child(STORE_PRODUCT).push().key

            if (newKey != null) {
                storeProduct.key = newKey
                storeProduct.lastEdited = System.currentTimeMillis()
                try {
                    userRef.child(STORE_PRODUCT).child(newKey).setValue(storeProduct)
                    sendBlocking(Result.success(storeProduct))
                } catch (e: Exception) {
                    sendBlocking(Result.failure(e))
                }
//                singleStoreProductFlow(newKey)
            }

        }

        awaitClose()
    }

    fun fetchAllStoreProduct() {
        val userRef = FirebaseDataRepository.userRef()

        userRef?.child(STORE_PRODUCT)?.orderByChild(FirebaseDataRepository.lastEdited)
            ?.addValueEventListener(object :
                ValueEventListener {

                override fun onDataChange(dataSnapshot: DataSnapshot) {

                    val storeProducts = arrayListOf<StoreProduct>()
                    dataSnapshot.children.forEach { customerSnapshot ->
                        customerSnapshot.getValue(StoreProduct::class.java)?.let {
                            storeProducts.add(it)
                        }
                    }
                    mAllStoreProduct.value = storeProducts
                }

                override fun onCancelled(databaseError: DatabaseError) {
                }
            })

    }

    fun fetchSingleStoreProduct(key: String) {

        if (key.isNotEmpty()) {
            val userRef = FirebaseDataRepository.userRef()

            userRef?.child(STORE_PRODUCT)?.child(key)
                ?.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {

                        val storeProduct =
                            dataSnapshot.getValue(StoreProduct::class.java) ?: StoreProduct()
                        mSingleStoreProduct.value = storeProduct
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                    }
                })
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun singleStoreProductFlow(key: String) = callbackFlow<Result<StoreProduct>> {
        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val storeProduct = dataSnapshot.getValue(StoreProduct::class.java)
                if (storeProduct != null) this@callbackFlow.sendBlocking(Result.success(storeProduct))
                else this@callbackFlow.sendBlocking(Result.failure(Exception("Value is null")))
            }

            override fun onCancelled(databaseError: DatabaseError) {
                this@callbackFlow.sendBlocking(Result.failure(databaseError.toException()))
            }
        }

        if (key.isNotEmpty()) {
            FirebaseDataRepository.userRef()
                ?.child(STORE_PRODUCT)?.child(key)
                ?.addListenerForSingleValueEvent(valueEventListener)

            awaitClose {
                FirebaseDataRepository.userRef()?.child(STORE_PRODUCT)
                    ?.removeEventListener(valueEventListener)
            }
        }
    }

    fun updateStoreProduct(storeProduct: StoreProduct) { //~
        val userRef = FirebaseDataRepository.userRef()
        if (userRef != null && storeProduct.key.isNotEmpty()) {
            storeProduct.lastEdited = System.currentTimeMillis()
            userRef.child(STORE_PRODUCT).child(storeProduct.key).setValue(storeProduct)
        }
    }

    fun deleteStoreProduct(key: String) {
        val userRef = FirebaseDataRepository.userRef()

        userRef?.child(STORE_PRODUCT_MODIFIED)?.orderByChild("storeProductKey")
            ?.equalTo(key)
            ?.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (snapshot in dataSnapshot.children) {
                        snapshot.ref.removeValue()
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })

        if (userRef != null && key.isNotEmpty()) {
            userRef.child(STORE_PRODUCT).child(key).removeValue()
        }
    }

    fun fetchEmptySingleStoreProduct() {
        mSingleStoreProduct.value = StoreProduct()
    }

    // store product modified

    fun saveStoreProductModified(storeProductModified: StoreProductModified) {
        val userRef = FirebaseDataRepository.userRef()
        if (userRef != null) {
            val newKey = userRef.child(STORE_PRODUCT_MODIFIED).push().key

            if (newKey != null) {
                storeProductModified.key = newKey
                storeProductModified.lastEdited = System.currentTimeMillis()
                userRef.child(STORE_PRODUCT_MODIFIED).child(newKey).setValue(storeProductModified)
            }
        }
    }

    fun fetchAllStoreProductModified() {
        val userRef = FirebaseDataRepository.userRef()
        userRef?.child(STORE_PRODUCT_MODIFIED)?.orderByChild(FirebaseDataRepository.lastEdited)
            ?.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
//                    val storeProductModifieds = arrayListOf<StoreProductModified>()
//                    dataSnapshot.children.forEach { customerSnapshot ->
//                        customerSnapshot.getValue(StoreProductModified::class.java)?.let {
//                            storeProductModifieds.add(it)
//                        }
//
//                    }
//                    mAllStoreProductModified.value = storeProductModifieds
                }

                override fun onCancelled(databaseError: DatabaseError) {

                }
            })
    }

    fun fetchSingleStoreProductModified(key: String) {
        if (key.isNotEmpty()) {
            val userRef = FirebaseDataRepository.userRef()
            userRef?.child(STORE_PRODUCT_MODIFIED)?.child(key)
                ?.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val storeProductModified =
                            dataSnapshot.getValue(StoreProductModified::class.java)
                        mSingleStoreProductModified.value = storeProductModified

                    }

                    override fun onCancelled(databaseError: DatabaseError) {}
                })
        }
    }

    fun getAllModifiedForThisStoreProduct(key: String) {
        fetchAllStoreProductModified()
        if (key.isNotEmpty()) {
            val me = FirebaseDataRepository.getUid()
            val userRef = FirebaseDataRepository.userRef()

            userRef?.child(STORE_PRODUCT_MODIFIED)?.orderByChild("storeProductKey")?.equalTo(key)
                ?.addValueEventListener(object : ValueEventListener {

                    override fun onDataChange(dataSnapshot: DataSnapshot) {

                        val storeProducts = arrayListOf<StoreProductModified>()

                        for (snapshot in dataSnapshot.children) {

                            val modified = snapshot.getValue(StoreProductModified::class.java)
                                ?: StoreProductModified()
                            storeProducts.add(modified)
                        }
                        mAllStoreProductModified.value = storeProducts
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                    }
                })
        }
    }

    fun fetchEmptyStoreProductModifedList() {
        mAllStoreProductModified.value = ArrayList()
    }
}