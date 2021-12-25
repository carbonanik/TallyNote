package com.carbondev.tallynote.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.carbondev.tallynote.datamodel.*
import com.carbondev.tallynote.utils.DateTimeString
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

object FirebaseDataRepository {

    private val mAllCustomer = MutableLiveData<ArrayList<Customer>>()
    val allCustomer: LiveData<ArrayList<Customer>> = mAllCustomer

    private val mAllSell = MutableLiveData<ArrayList<Sell>>()
    val allSell: LiveData<ArrayList<Sell>> = mAllSell

    private val mAllNote = MutableLiveData<ArrayList<Note>>()
    val allNote: LiveData<ArrayList<Note>> = mAllNote

    private val mQueryLibraryList = MutableLiveData<ArrayList<Library>>()
    val queryLibraryList: LiveData<ArrayList<Library>> = mQueryLibraryList


    // single *******************************************************

    private val mSingleCustomer = MutableLiveData<Customer>()
    val singleCustomer: LiveData<Customer> = mSingleCustomer

    private val mOpenNote = MutableLiveData<Note>()
    val openNote: LiveData<Note> = mOpenNote

    private val mSingleSell = MutableLiveData<Sell>()
    val singleSell: LiveData<Sell> = mSingleSell

    private val mOwnerName = MutableLiveData<String>()
    val ownerName: LiveData<String> = mOwnerName


    private var auth = FirebaseAuth.getInstance()
    private const val lastEdited = "lastEdited"

    // Customer **********************************************

    fun saveCustomer(customer: Customer) {
        val userRef = userRef()
        val newKey: String?

        if (userRef != null) {
            newKey = userRef.child(CUSTOMER).push().key
            if (!newKey.isNullOrEmpty()) {
                customer.key = newKey
                customer.lastEdited = System.currentTimeMillis()
                userRef.child(CUSTOMER).child(newKey).setValue(customer)
            }
        }
    }

    fun saveOldCustomer(oldCustomer: Customer) {
        val userRef = userRef()

        if (userRef != null) {
            if (oldCustomer.key.isNotEmpty()) {
                userRef.child(CUSTOMER).child(oldCustomer.key).setValue(oldCustomer)
            }
        }
    }

    fun updateCustomer(customer: Customer) { //~
        val userRef = userRef()
        if (userRef != null) {
            if (customer.key.isNotEmpty()) {
                customer.lastEdited = System.currentTimeMillis()
                userRef.child(CUSTOMER).child(customer.key).setValue(customer)
            }
        }
    }

    fun deleteCustomer(customerKey: String) {
        val ur = userRef()
        val q = ur?.child(SELL)?.orderByChild("customerKey")?.equalTo(customerKey)
        q?.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (single in snapshot.children) ur.child(SELL).child(single.key!!).removeValue()
            }

            override fun onCancelled(error: DatabaseError) {}

        })

        ur?.child(CUSTOMER)?.child(customerKey)?.removeValue()
    }

    fun fetchAllCustomer() {
        val userRef = userRef()

        userRef?.child(CUSTOMER)?.orderByChild(lastEdited)?.addValueEventListener(object :
            ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val customers = arrayListOf<Customer>()
                dataSnapshot.children.forEach { customerSnapshot ->
                    val customer = customerSnapshot.getValue(Customer::class.java)
                    customers.add(customer!!)
                }
                mAllCustomer.value = customers
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        })

    }

    fun fetchSingleCustomer(key: String) {

        if (key.isNotEmpty()) {
            val userRef = userRef()

            userRef?.child(CUSTOMER)?.child(key)
                ?.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {

                        val customer = dataSnapshot.getValue(Customer::class.java) ?: Customer()
                        mSingleCustomer.value = customer
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                    }
                })
        }
    }

    fun fetchEmptySingleCustomer() {
        mSingleCustomer.value = Customer()
    }


    // sell *******************************************************

    fun saveSell(sell: Sell) {
        val userRef = userRef()

        if (userRef != null) {

            val newKey = userRef.child(SELL).push().key

            if (newKey != null) {
                sell.key = newKey
                userRef.child(SELL).child(newKey).setValue(sell).addOnSuccessListener {
                }
                fetchSingleSell(newKey)
            }
        }
    }

    fun updateSell(sell: Sell) {
        val userRef = userRef()
        if (sell.key.isNotEmpty()) {
            userRef?.child(SELL)?.child(sell.key)?.setValue(sell)?.addOnSuccessListener {
            }
        }
    }

    fun fetchAllSellForThisCustomer(customerKey: String) {
        fetchAllSell()
        if (customerKey.isNotEmpty()) {
            val userRef = userRef()

            userRef?.child(SELL)?.orderByChild("customerKey")?.equalTo(customerKey)
                ?.addValueEventListener(object : ValueEventListener {

                    override fun onDataChange(dataSnapshot: DataSnapshot) {

                        val sells = arrayListOf<Sell>()

                        for (sellSnapshot in dataSnapshot.children) {

                            val sell = sellSnapshot.getValue(Sell::class.java) ?: Sell()
                            sells.add(sell)
                        }
                        mAllSell.value = sells
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                    }
                })
        }
    }

    private fun fetchAllSell() {
        val userRef = userRef()
        userRef?.child(SELL)?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {}
            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun fetchSingleSell(key: String) {
        if (key.isNotEmpty()) {
            val userRef = userRef()

            userRef?.child(SELL)?.child(key)?.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {

                    val sell = dataSnapshot.getValue(Sell::class.java) ?: Sell()
                    mSingleSell.value = sell
                }

                override fun onCancelled(databaseError: DatabaseError) {
                }
            })
        }
    }

    fun fetchEmptySellList() {
        mAllSell.value = arrayListOf()
    }

    fun fetchEmptySell() {
        mSingleSell.value = Sell()
    }


    // note ***************************************************

    fun saveNote(note: Note) {
        val userRef = userRef()
        if (userRef != null) {

            val newKey = userRef.child(NOTE).push().key

            if (newKey != null) {
                note.key = newKey
                note.lastEdited = System.currentTimeMillis()
                userRef.child(NOTE).child(newKey).setValue(note)

                fetchSingleNote(newKey)


            }

        }

    }

    fun updateNote(note: Note) { //~
        val userRef = userRef()
        if (userRef != null && note.key.isNotEmpty()) {
            note.lastEdited = System.currentTimeMillis()
            userRef.child(NOTE).child(note.key).setValue(note)
        }
    }

    fun fetchAllNote() {
        val userRef = userRef()

        userRef?.child(NOTE)?.orderByChild(lastEdited)
            ?.addValueEventListener(object : ValueEventListener {

                override fun onDataChange(dataSnapshot: DataSnapshot) {

                    val notes = arrayListOf<Note>()
                    for (noteSnapshot in dataSnapshot.children) {

                        val note = noteSnapshot.getValue(Note::class.java) ?: Note()
                        notes.add(note)
                    }
                    mAllNote.value = notes
                }

                override fun onCancelled(databaseError: DatabaseError) {
                }
            })
    }

    fun fetchSingleNote(key: String) {
        if (key.isNotEmpty()) {
            val userRef = userRef()

            userRef?.child(NOTE)?.child(key)?.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {

                    val note = dataSnapshot.getValue(Note::class.java) ?: Note()
                    mOpenNote.value = note
                }

                override fun onCancelled(databaseError: DatabaseError) {
                }
            })
        }

    }

    fun deleteNote(noteKey: String) {

        if (noteKey.isNotEmpty()) {
            val userRef = userRef()

            userRef?.child(NOTE)?.child(noteKey)?.removeValue()
        }
    }

    fun fetchEmptyNote() {
        mOpenNote.value = Note()
    }

    // library ******************************************************
    fun getLibraryFilteredList(queryText: String, isName: Boolean) {
        val libraryRef = if (isName)
            userRef()?.child(LIBRARY_NAME)
        else
            userRef()?.child(LIBRARY_DETAIL)

        if (queryText.isBlank()) {
            libraryRef?.addValueEventListener(object : ValueEventListener {

                override fun onDataChange(dataSnapshot: DataSnapshot) {

                    val library = arrayListOf<Library>()

                    for (sellSnapshot in dataSnapshot.children) {

                        val l = sellSnapshot.getValue(Library::class.java) ?: Library()
                        library.add(l)
                    }
                    mQueryLibraryList.value = library
                }

                override fun onCancelled(databaseError: DatabaseError) {
                }
            })
        } else {
            libraryRef?.orderByChild("detail")?.startAt(queryText)
                ?.addValueEventListener(object : ValueEventListener {

                    override fun onDataChange(dataSnapshot: DataSnapshot) {

                        val library = arrayListOf<Library>()

                        for (sellSnapshot in dataSnapshot.children) {

                            val l = sellSnapshot.getValue(Library::class.java) ?: Library()
                            library.add(l)
                        }
                        mQueryLibraryList.value = library
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                    }
                })
        }
    }

    fun addLibraryItem(text: String, isName: Boolean) {

        val library = Library(
            detail = text,
            usedCount = 1
        )

        val userRef = if (isName)
            userRef()?.child(LIBRARY_NAME)
        else
            userRef()?.child(LIBRARY_DETAIL)

        val newKey = userRef?.push()?.key

        if (newKey != null) {
            userRef.child(newKey).setValue(library)
        }
    }

    fun saveNameToFirebase(userName: String) {
        if (userName.isNotEmpty()) {
            val userRef = userRef()
            userRef?.child("data")?.child("name")?.setValue(userName)
        }
    }

    fun fetchOwnerName() {
        val userRef = userRef()

        userRef?.child("data")?.child("name")
            ?.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    mOwnerName.value = dataSnapshot.value as String?
                }
            })
    }

    private fun getUid(): String? = auth.currentUser?.uid

    private fun userRef(): DatabaseReference? { // ~
        var ref: DatabaseReference? = null
        val uid: String? = getUid()

        if (!uid.isNullOrEmpty()) {
            ref = FirebaseDatabase.getInstance().reference.child(USERS).child(uid)
        }
        return ref
    }
}

