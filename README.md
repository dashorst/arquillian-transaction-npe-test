# Arquillian TransactionHandler NPE testcase

This project is a testcase for Arquillian's TransactionHandler throwing
a NullPointerException when outside a testmethod CDI setup fails,
masking the original exception and making development of test cases
neigh impossible.

See [NullPointerInTransactionHandlerTest.java](com/example/arquillian/NullPointerInTransactionHandlerTest.java)
for the detailed test case. This test will always fail (the setup
before the test method fails and this should never result in a succesful 
test)
