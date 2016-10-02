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

  }
}
