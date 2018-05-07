# Support Form Challenge

## Task

A naive support form handler app.  

Stores collected form data in a SQLite database and sends notifications emails using Mailgun.com

In order to function, requires a file "mail-settings.txt" to be added to root that contains a mailgun domain, API key, and the target email to notify.  *Tomorrow* I will be setting it up to accept those as command-line options on startup.
