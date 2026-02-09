package com.server.controller;

import com.server.dao.BibleVerseDao;
import com.server.model.BibleVerse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@CrossOrigin
@RequestMapping("/api/bible-verses")
public class BibleVerseController {

    private final BibleVerseDao bibleVerseDao;

    public BibleVerseController(BibleVerseDao bibleVerseDao) {
        this.bibleVerseDao = bibleVerseDao;
    }

    @GetMapping("/daily")
    public BibleVerse getDailyVerse() {
        BibleVerse verse = bibleVerseDao.getDailyVerse();
        if (verse == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No daily verse available.");
        }
        return verse;
    }

    @GetMapping("/random")
    public BibleVerse getRandomVerse() {
        BibleVerse verse = bibleVerseDao.getRandomVerse();
        if (verse == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No random verse available.");
        }
        return verse;
    }
}
