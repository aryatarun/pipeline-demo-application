node {

def mvnHome = tool 'M3'

  git url: 'git@bitbucket.org:thomasanderer/pipeline-demo.git'

  stage ('Versioning') {

    sh """
      echo "MVN=`${mvnHome}/bin/mvn -q -Dexec.executable="echo" -Dexec.args='\${project.version}' --non-recursive org.codehaus.mojo:exec-maven-plugin:1.3.1:exec`" > version.properties
      echo "COMMIT=`git rev-parse HEAD --short`"
    """
    def version = readProperties file: 'version.properties'
    echo "Pom-Version=$version"

    def now = new Date()
    def date = now.format("yyyyMMdd", TimeZone.getTimeZone('UTC'))
    def time = now.format("HHmmss", TimeZone.getTimeZone('UTC'))
    def newVersion = "${version['MVN']}-${date}_${time}_${version['COMMIT']}"


  }

  stage ('CI-Build') {

  //sh "${mvnHome}/bin/mvn -B verify"

  //junit 'target/surefire-reports/**.xml'

  //step([$class: 'FindBugsPublisher', canComputeNew: false, defaultEncoding: '', excludePattern: '', healthy: '', includePattern: '', pattern: '**/findbugs.xml', unHealthy: ''])

  //step([$class: 'AnalysisPublisher', canComputeNew: false, defaultEncoding: '', healthy: '', unHealthy: ''])

  }
}
