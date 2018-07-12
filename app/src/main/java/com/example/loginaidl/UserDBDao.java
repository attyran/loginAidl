package com.example.loginaidl;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface UserDBDao {
    @Query("SELECT * FROM userdb")
    List<UserDB> getAll();

    @Query("SELECT * FROM userdb WHERE uid IN (:userIds)")
    List<UserDB> loadAllByIds(int[] userIds);

    @Query("SELECT * FROM userdb WHERE username LIKE :name LIMIT 1")
    UserDB find(String name);

    @Insert
    void insertAll(UserDB... users);

    @Delete
    void delete(UserDB user);

    @Update
    void update(UserDB user);
}
