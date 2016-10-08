# Continuous Delivery with Jenkins Pipeline
* Explain a bit about Jenkins Pipeline
* Fork the Repository of the application (https://github.com/cloudfoundry-samples/pong_matcher_spring.git)
* Add a Jenkinsfile
* Maven build

## Order Jenkinsfile
* echo
* stage
* node
* Commit-Stage git / maven
* versioning (methods?)
* junit / findbugs
* deploy to test (methods?)
* acceptance-test with docker
* parallel manual step
* b/g deployment (external file? load?)

### Intro
 - Create Jenkins pipeline job
   Edit pipeline inline
   ```
     stage("Stage A") {
       node {
         echo "Hello A"
       }
     }
     stage ("Stage B") {
       node {
         echo ("Hello B")
       }
     }
         
   ```
   
  - Switch to Jenkinsfile
  Select git repo ``
  `Jenkinsfile`
  Goto IntelliJ
  Maven job
  ```
  
  ```
  





## Links

### Continuous Delivery
### Jenkins Pipeline
* [https://github.com/jenkinsci/pipeline-plugin/blob/master/TUTORIAL.md](Jenkins Pipeline Tutorial)
