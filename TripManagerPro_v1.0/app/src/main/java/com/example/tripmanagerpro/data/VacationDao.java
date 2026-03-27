package com.example.tripmanagerpro.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface VacationDao {

    @Insert
    long insert(Vacation vacation);

    @Update
    int update(Vacation vacation);

    @Delete
    int delete(Vacation vacation);

    @Query("SELECT * FROM vacations ORDER BY startDate")
    List<Vacation> getAllVacations();

    @Query("SELECT * FROM vacations WHERE id = :id")
    Vacation getVacationById(int id);

    @Query("SELECT * FROM vacations " +
            "WHERE title LIKE '%' || :query || '%' " +
            "OR hotel LIKE '%' || :query || '%' " +
            "ORDER BY startDate")
    List<Vacation> searchVacations(String query);
}