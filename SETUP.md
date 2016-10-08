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
  extract method
  
### Automatic Versioning

```
private void versioning(mvnHome) {
    sh """
        echo "MVN=`${mvnHome}/bin/mvn -q -Dexec.executable="echo" -Dexec.args='\${project.version}' --non-recursive org.codehaus.mojo:exec-maven-plugin:1.3.1:exec`" > version.properties
        echo "COMMIT=`git rev-parse --short HEAD`" >> version.properties
        echo "TIMESTAMP=`date +\"%Y%M%d_%H%M%S\"`" >> version.properties
        """
    def pomVersion = readProperties file: 'version.properties'
    echo "Pom-Version=$pomVersion"

    def version = "${pomVersion['MVN']}-${pomVersion['TIMESTAMP']}_${pomVersion['COMMIT']}"
    echo "Automated version: ${version}"

    sh "${mvnHome}/bin/mvn versions:set -DnewVersion=\"${version}\""

    pushPomBuildTag(version)

    return version
}

private void pushPomBuildTag(version) {
    sh """
        git add pom.xml
        git commit -m "versioning $version"
        git tag BUILD_$version
        git push origin BUILD_$version
    """
}
```

refactor to
```
def version = ""

node {
    def mvnHome = tool 'M3'
    stage('Checkout') {
        git url: 'git@bitbucket.org:thomasanderer/pipeline-demo.git'
    }
    stage('Versioning') {
        version = versioning(mvnHome)
    }
    stage('CI-Build') {
        executeCiBuild(mvnHome, version)
    }
}
```
  
### Acceptance tests - Deploy

```
stage('Acceptance') {
    deployToCf(version)
}

private void deployToCf(version) {
    node {
        unstash name: 'artifacts'
        withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: '3cd9dd1f-8015-4bc1-9e2b-329c6fa267de', passwordVariable: 'CF_PASSWORD', usernameVariable: 'CF_USERNAME']]) {
            sh """
        cf login -a https://api.aws.ie.a9s.eu -o thomas_rauner_andrena_de -s test -u $CF_USERNAME -p $CF_PASSWORD
        set +e
        cf create-service a9s-postgresql postgresql-single-small mysql
        set -e
        cf push -n cf-demo-andrena-test -p \"target/pong-matcher-spring-${version}.jar\"
      """
        }
    }
}
```


### Acceptance tests - Run

```
stage('Acceptance') {
    deployToCf(version)
    runAcceptanceTest()
}


private void runAcceptanceTest() {
    node {
        def testHost = "http://cf-demo-andrena-test.aws.ie.a9sapp.eu"

        git url: 'git@bitbucket.org:thomasanderer/pongmatcher-acceptance-fixed.git'

        sh """#/bin/bash -ex
            docker build -t pong-matcher-acceptance .
            docker run --name acceptance --rm -e \"HOST=$testHost\" pong-matcher-acceptance
        """
    }
}
```

### Parallel Manual tests
```
stage('Acceptance') {
    deployToCf(version)

    parallel automated: {
        runAcceptanceTest()
    }, manual: {
        manualAcceptanceCheck()
    }
}

private void manualAcceptanceCheck() {
    node {
        input("Manual acceptance tests successfully?")
    }
}
```

### Blue-Green-Deploy to Production
```
node {
    stage('Production') {
        unstash name: 'artifacts'
        deployer = load 'Deploy.Jenkinsfile'
        blueGreenDeploy("cf-demo-andrena-prod", version, "target/pong-matcher-spring-${version}.jar", "cf-demo-andrena-prod")
    }
}
```
## Links

### Continuous Delivery
### Jenkins Pipeline
* [https://github.com/jenkinsci/pipeline-plugin/blob/master/TUTORIAL.md](Jenkins Pipeline Tutorial)
