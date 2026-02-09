package com.server.dao;

import com.server.model.BibleVerse;

public interface BibleVerseDao {

    BibleVerse getDailyVerse();
    
    BibleVerse getRandomVerse();
}
