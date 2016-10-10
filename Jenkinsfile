node {
    stage('CI-Build') {
        def mvnHome = tool 'M3'

        git url: 'git@bitbucket.org:thomasanderer/pipeline-demo.git'
        sh "${mvnHome}/bin/mvn -B verify"
        junit 'target/surefire-reports/**.xml'
    }
}