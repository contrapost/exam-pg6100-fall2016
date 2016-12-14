1. quizImpl is deployed on OpenShift - http://jeequiz-jeequiz.44fs.preview.openshiftapps.com/quiz/.
NB! You need to change path in input window accordingly to the web address as I didn't change application config file
considering that project will be checked locally.

2. Tests for gameSoap are implemented but marked as "@Ignore" because of some mismatch with wiremock configurations.
Stubbed responses work but server returns 405.

3. I implemented solid number of tests for extra functionalities, user errors in quizImpl. Unfortunately it wasn't
 still enough to reach the 90% threshold.

