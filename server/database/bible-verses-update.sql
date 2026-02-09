START TRANSACTION;

DROP TABLE IF EXISTS bible_verses CASCADE;

CREATE TABLE bible_verses (
    id SERIAL PRIMARY KEY,
    book VARCHAR(50) NOT NULL,
    chapter INT NOT NULL,
    verse VARCHAR(20) NOT NULL,
    text TEXT NOT NULL,
    version VARCHAR(20) DEFAULT 'NIV'
);

INSERT INTO bible_verses (book, chapter, verse, text, version) VALUES
('1 Corinthians', 16, '14', 'Let all that you do be done in love.', 'NIV'),
('Jeremiah', 29, '11', 'For I know the plans I have for you, plans to prosper you and not to harm you, plans to give you hope and a future.', 'NIV'),
('Joshua', 1, '9', 'Be strong and courageous. Do not be afraid; do not be discouraged, for the Lord your God will be with you wherever you go.', 'NIV'),
('1 John', 4, '19', 'We love because He first loved us.', 'NIV'),
('Psalm', 23, '1', 'The Lord is my shepherd, I lack nothing.', 'NIV'),
('1 Peter', 5, '7', 'Cast all your anxiety on him because he cares for you.', 'NIV'),
('Philippians', 4, '13', 'I can do all this through him who gives me strength.', 'NIV'),
('Galatians', 5, '22', 'But the fruit of the Spirit is love, joy, peace, forbearance, kindness, goodness, faithfulness.', 'NIV'),
('Proverbs', 3, '5', 'Trust in the Lord with all your heart and lean not on your own understanding.', 'NIV'),
('Romans', 8, '28', 'And we know that in all things God works for the good of those who love him.', 'NIV');

COMMIT TRANSACTION;
