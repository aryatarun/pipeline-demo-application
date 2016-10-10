node {
    stage('CI-Build') {
        def mvnHome = tool 'M3'
        sh "${mvnHome}/bin/mvn -B verify"
        junit 'target/surefire-reports/**.xml'
        step([$class: 'FindBugsPublisher', canComputeNew: false, defaultEncoding: '', excludePattern: '', healthy: '', includePattern: '', pattern: '**/findbugs.xml', unHealthy: ''])

    }
}