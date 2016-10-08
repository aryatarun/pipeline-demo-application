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
   
### CI-Build
Switch to Jenkinsfile
Select git repo `git@bitbucket.org:thomasanderer/pipeline-demo.git`
`Jenkinsfile`
Goto IntelliJ

```
      node {
          stage ('CI-Build') {
              def mvnHome = tool 'M3'
              git url: 'git@bitbucket.org:thomasanderer/pipeline-demo.git'
              
              sh "${mvnHome}/bin/mvn -B verify"
              junit 'target/surefire-reports/**.xml'
              step([$class: 'FindBugsPublisher', canComputeNew: false, defaultEncoding: '', excludePattern: '', healthy: '', includePattern: '', pattern: '**/findbugs.xml', unHealthy: ''])
              stash includes: "manifest.yml, target/pong-matcher-spring-${version}.jar", name: 'artifacts'
          }
      }
  ```
  
### Automatic Versioning
  





## Links

### Continuous Delivery
### Jenkins Pipeline
* [https://github.com/jenkinsci/pipeline-plugin/blob/master/TUTORIAL.md](Jenkins Pipeline Tutorial)
