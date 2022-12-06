package com.example.music_buddy.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.music_buddy.ChatuserAdapter
import com.example.music_buddy.Data
import com.example.music_buddy.R
import com.example.music_buddy.User
import com.parse.FindCallback
import com.parse.ParseException
import com.parse.ParseQuery
import com.parse.ParseUser

class chatlistFragment: Fragment(R.layout.fragment_chatlist) {

    lateinit var friendsRecyclerView: RecyclerView

    lateinit var adapter: ChatuserAdapter

    var allFriends: MutableList<Data> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chatlist, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        friendsRecyclerView = view.findViewById(R.id.friendsRecyclerView)

        adapter = ChatuserAdapter(requireContext(), allFriends)
        friendsRecyclerView.adapter = adapter

        friendsRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        allFriends.clear()
        queryFriends()


    }

    open fun queryFriends() {
        // Specify which class to query

//        try {
        val query: ParseQuery<Data> = ParseQuery.getQuery(Data::class.java)
        query.whereEqualTo(Data.KEY_EMAIL, ParseUser.getCurrentUser().username)

//        query.addDescendingOrder("createdAt")
//        query.include(Data.KEY_USER)

        query.findInBackground(object : FindCallback<Data> {
            override fun done(dataUsers: MutableList<Data>?, e: ParseException?) {
                if (e != null) {
                    Log.e(TAG, "Error fetching user data")
                } else {
                    Log.i(TAG, "Success fetching user data")
                    if (dataUsers != null && dataUsers.isNotEmpty()) {
                        val dataUser = dataUsers.first()
                        Log.i(
                            TAG,
                            "username: " + dataUser.getUser()?.objectId
                                    + " , friendsArray: " + dataUser.getFriendsArray()
                        )
                        var friendsListJSONArray = dataUser.getFriendsArray()
                        if (friendsListJSONArray != null) {
                            for (i in 0 until friendsListJSONArray.length()) {
                                val friendID = friendsListJSONArray.getString(i)
//                                    Log.i(TAG, "friendID: " + friendID)
                                queryParseUser(friendID)

                            }
                        }
                    }
                }
            }
        })
    }

    fun queryParseUser(userID: String){
//        var userDataPointer: Data = Data()
        val query: ParseQuery<Data> = ParseQuery.getQuery(Data::class.java)
        query.whereEqualTo(Data.KEY_USERNAME, userID)

        query.findInBackground(object : FindCallback<Data> {
            override fun done(dataUsers: MutableList<Data>?, e: ParseException?) {
                if (e != null) {
                    Log.e(TAG, "Error fetching parseUser data")
                } else {
                    Log.i(TAG, "Success fetching parseUser data")
                    Log.i(
                        TAG,
//                                        + " , singleArray: " + dataUser
                        " , Fullarray: " + dataUsers
                    )
                    if (dataUsers != null && dataUsers.isNotEmpty()) {
//                        userDataPointer = dataUsers.first()
                        allFriends.add(dataUsers.first())
                        adapter.notifyDataSetChanged()
                        Log.i(
                            TAG,
                            ".first test friend objectID: " + dataUsers.first()

                        )
                        for (dataUser in dataUsers) {
                            Log.i(
                                TAG,
                                "Friend objectID: " + dataUser.getUsername()

                            )
                        }
                    }
                }
            }
        })
    }

    companion object {
        const val TAG = "chatlistFragment"
    }
}