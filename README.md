# Support Form Challenge

## Task

Build a request form for a support page.

## Overview

Your challenge is to build a naive yet complete Clojure web application. It should include one page: a form for collecting support requests. Here's an example:

![](example.png?raw=true)

Your application should collect a request category, a question body, a file, and an email address, as shown in the image above (don't bother with the account URL) and send it to the server for processing. On the server, do two things with the data: 1) save it to a SQL database, and 2) forward the information as an email to support@teachbanzai.com.

Write your application in Clojure. Use Leiningen as your build tool. For routing and templating consider Ring, Hiccup, or Selmer. For databases there are plenty of options, including JDBC for PostgreSQL. For email, good luck.

The code you produce may be used on a real website. You will, therefore, be paid $150.00 for a _working_ solution, whether or not it's chosen.

### Tips

Avoid fanciness. Use traditional web standards where possible. Limit your use of 3rd party libraries whereever practical.

### Questions

Don't hesitate to ask. While I won't provide tips for solving the problem, I can clarify the rules.
