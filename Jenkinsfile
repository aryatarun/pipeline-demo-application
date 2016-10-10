node {
    stage('CI-Build') {
        def mvnDir = tool 'M3'
        sh '''
            mvn verify
        '''
    }
}