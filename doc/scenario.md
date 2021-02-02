# Testing in Spring scenario

It is requested to implement the needed tests to verify the correct functionality of an application that manages an online bookstore. The application code is provided.

Following tests are required to implement:

* Unit tests with MockMVC:
  * It is possible to retrieve all books (as non logged user).
  * It is possible to create a new book (as logged user).
  * It is possible to delete a book (as admin).

* E2E tests with RESTAssured:
  * It is possible to retrieve all books (as non logged user).
  * It is possible to create a new book (as logged user) and check that it has been created.
  * It is possible to delete a book (as admin) and check that it has been deleted.

* Tests with WebTestClient:
  * Implement previous unit and REST API tests with WebTestClient.

**NOTES:**
* Tests must be independant of each other (not depend on information that other tests have created or deleted). Besides being an anti-pattern, JUnit does not
guarantees the order of execution by default.
* Application uses HTTPS and Basic Auth.
* Tests must be grouped by different classes or packages because they are different type of tests.
* In unit tests are mandatory the Mocks usage, because persistence is done using a H2 database (and we want to avoid it in this type of test).
