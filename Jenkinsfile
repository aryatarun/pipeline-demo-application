def version = ""

node {

def mvnHome = tool 'M3'

  git url: 'git@bitbucket.org:thomasanderer/pipeline-demo.git'

  stage ('Versioning') {

    sh """
      echo "MVN=`${mvnHome}/bin/mvn -q -Dexec.executable="echo" -Dexec.args='\${project.version}' --non-recursive org.codehaus.mojo:exec-maven-plugin:1.3.1:exec`" > version.properties
      echo "COMMIT=`git rev-parse --short HEAD`" >> version.properties
      echo "TIMESTAMP=`date +\"%Y%M%d_%H%M%S\"`" >> version.properties
    """
    def pomVersion = readProperties file: 'version.properties'
    echo "Pom-Version=$pomVersion"

    version = "${pomVersion['MVN']}-${pomVersion['TIMESTAMP']}_${pomVersion['COMMIT']}"
    echo "Automated version: ${version}"

    sh "${mvnHome}/bin/mvn versions:set -DnewVersion=\"${version}\""

    //Should push the version back to repo
  }

  stage ('CI-Build') {

    sh "${mvnHome}/bin/mvn -B verify"

    junit 'target/surefire-reports/**.xml'

    step([$class: 'FindBugsPublisher', canComputeNew: false, defaultEncoding: '', excludePattern: '', healthy: '', includePattern: '', pattern: '**/findbugs.xml', unHealthy: ''])

    step([$class: 'AnalysisPublisher', canComputeNew: false, defaultEncoding: '', healthy: '', unHealthy: ''])

    stash includes: "manifest.yml, target/pong-matcher-spring-${version}.jar", name: 'artifacts'
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
        cf push -n cf-demo-andrena-test -p \"target/pong-matcher-spring-${version}.jar\" -t 180 -b https://github.com/cloudfoundry/java-buildpack.git
      """
    }

    // This step should not normally be used in your script. Consult the inline help for details.
//withDockerContainer(args: '-e "HOST=cf-demo-andrena-test.aws.ie.a9sapp.eu"', image: 'docker.gocd.cf-app.com:5000/pong-matcher-acceptance') {
    // some block
//}

    def testHost = "cf-demo-andrena-test.aws.ie.a9sapp.eu"

    sh "docker run --rm -i -t -e \"HOST=${testHost}\" docker.gocd.cf-app.com:5000/pong-matcher-acceptance"
    //docker.image('docker.gocd.cf-app.com:5000/pong-matcher-acceptance').inside('-e "HOST=cf-demo-andrena-test.aws.ie.a9sapp.eu"', image: 'docker.gocd.cf-app.com:5000/pong-matcher-acceptance') {
      //git url: 'https://github.com/cloudfoundry-samples/pong_matcher_acceptance.git'
      //sh 'mvn -B clean install'
    //}
  }

  //Acceptance test (maybe cf?)
  //Parallel performance test (maybe curl?)
  //Manual step -> manual acceptance
  //release to production (cf b/g?)
}



node {
  stage ('Production') {
  unstash name: 'artifacts'
  //cf curl /v2/routes?q=host:cf-demo-andrena-test | jq -r ".resources[].metadata.url"
  //cf curl /v2/routes/09644b29-b348-4629-9c92-d3820f2633be/apps | jq -r ".resources[].entity.name"

    withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: '3cd9dd1f-8015-4bc1-9e2b-329c6fa267de', passwordVariable: 'CF_PASSWORD', usernameVariable: 'CF_USERNAME']]) {
      sh """#!/bin/bash -e -x
        mkdir -p cf_home
        export CF_HOME=`pwd`/cf_home
        cf login -a https://api.aws.ie.a9s.eu -o thomas_rauner_andrena_de -s production -u $CF_USERNAME -p $CF_PASSWORD
        set +e
        cf create-service a9s-postgresql postgresql-single-small mysql
        set -e

        route=${cf curl /v2/routes?q=host:cf-demo-andrena-prod | jq -r ".resources[].metadata.url"}
        bound_apps=${cf curl $route/apps | jq -r ".resources[].entity.name"}
        for bound_app in $bound_apps; do
          echo "Bound App: $bound_app"
        done

        appname=cf-demo-andrena-prod-${version}
        approute=cf-demo-andrena-prod-${version}
        domain=aws.ie.a9sapp.eu
        mainroute=cf-demo-andrena-prod
        cf push $appname -n $approute -p \"target/pong-matcher-spring-${version}.jar\" -t 180 -b https://github.com/cloudfoundry/java-buildpack.git
        cf map-route $appname $domain -n $mainroute
        for boundapp in $bound_apps; do
          cf unmap-route $boundapp $domain -n $mainroute
          cf scale -i 0 $boundapp
          cf stop $boundapp
          cf delete $boundapp
        done
      """
    }

  }

  //release to production (cf b/g?)
}
