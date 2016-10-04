node {

def mvnHome = tool 'M3'

  git url: 'git@bitbucket.org:thomasanderer/pipeline-demo.git'

  stage ('Versioning') {

    sh """
      echo "MVN=`${mvnHome}/bin/mvn -q -Dexec.executable="echo" -Dexec.args='\${project.version}' --non-recursive org.codehaus.mojo:exec-maven-plugin:1.3.1:exec`" > version.properties
      echo "COMMIT=`git rev-parse --short HEAD`" >> version.properties
      echo "TIMESTAMP=`date +\"%Y%M%d_%H%M%S\"`" >> version.properties
    """
    def version = readProperties file: 'version.properties'
    echo "Pom-Version=$version"

    def newVersion = "${version['MVN']}-${version['TIMESTAMP']}_${version['COMMIT']}"
    echo "Automated version: ${newVersion}"

    sh "${mvnHome}/bin/mvn versions:set -DnewVersion=\"${newVersion}\""

    //Should push the version back to repo
  }

  stage ('CI-Build') {

    sh "${mvnHome}/bin/mvn -B verify"

    junit 'target/surefire-reports/**.xml'

    step([$class: 'FindBugsPublisher', canComputeNew: false, defaultEncoding: '', excludePattern: '', healthy: '', includePattern: '', pattern: '**/findbugs.xml', unHealthy: ''])

    step([$class: 'AnalysisPublisher', canComputeNew: false, defaultEncoding: '', healthy: '', unHealthy: ''])

    stash includes: 'manifest.yml, target/pong-matcher-spring-*.jar', name: 'artifacts'
  }
}

node {
  stage ('Acceptance') {
  unstash name: 'artifacts'
    withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: '3cd9dd1f-8015-4bc1-9e2b-329c6fa267de', passwordVariable: 'CF_PASSWORD', usernameVariable: 'CF_USERNAME']]) {
      sh """
        cf login -a https://api.aws.ie.a9s.eu -o thomas_rauner_andrena_de -s test -u $CF_USERNAME -p $CF_PASSWORD
        set +e
        cf create-service a9s-postgresql postgresql-single-small mysql
        set -e
        cf push -n cf-demo-andrena-test -p \"target/pong-matcher-spring-${newVersion}.jar\"
      """
    }

    // This step should not normally be used in your script. Consult the inline help for details.
//withDockerContainer(args: '-e "HOST=cf-demo-andrena-test.aws.ie.a9sapp.eu"', image: 'docker.gocd.cf-app.com:5000/pong-matcher-acceptance') {
    // some block
//}


    docker.image('docker.gocd.cf-app.com:5000/pong-matcher-acceptance').inside('-e "HOST=cf-demo-andrena-test.aws.ie.a9sapp.eu"', image: 'docker.gocd.cf-app.com:5000/pong-matcher-acceptance') {
      //git url: 'https://github.com/cloudfoundry-samples/pong_matcher_acceptance.git'
      //sh 'mvn -B clean install'
    }
  }

  //Acceptance test (maybe cf?)
  //Parallel performance test (maybe curl?)
  //Manual step -> manual acceptance
  //release to production (cf b/g?)
}
