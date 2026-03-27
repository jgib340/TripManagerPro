package com.example.tripmanagerpro.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ExcursionDao {

    @Insert
    long insert(Excursion excursion);

    @Update
    int update(Excursion excursion);

    @Delete
    int delete(Excursion excursion);

    @Query("SELECT * FROM excursions WHERE vacationId = :vacationId ORDER BY date")
    List<Excursion> getExcursionsForVacation(int vacationId);

    @Query("SELECT COUNT(*) FROM excursions WHERE vacationId = :vacationId")
    int countExcursionsForVacation(int vacationId);

    @Query("SELECT * FROM excursions WHERE id = :excursionId LIMIT 1")
    Excursion getExcursionById(int excursionId);
}