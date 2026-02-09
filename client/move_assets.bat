@echo off
set "SRC=C:\Users\gabri\.gemini\antigravity\brain\ded24fe0-7875-4320-94b0-e7a96423f847"
set "DEST=D:\workspace\Prayly\client\public\badges"

if not exist "%DEST%" mkdir "%DEST%"

copy "%SRC%\prayer_badge_icon_1770164536146.png" "%DEST%\prayer.png"
copy "%SRC%\streak_badge_icon_1770164548840.png" "%DEST%\streak.png"
copy "%SRC%\request_badge_icon_1770164561915.png" "%DEST%\request.png"
copy "%SRC%\time_badge_icon_1770164573892.png" "%DEST%\time.png"

echo Done.
