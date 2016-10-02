

node {
  stage 'CI-Stage'

  git url: 'git@bitbucket.org:thomasanderer/pipeline-demo.git'


  def mvnHome = tool 'M3'
  sh "${mvnHome}/bin/mvn -B verify"

  junit ''

  step([$class: 'FindBugsPublisher', canComputeNew: false, defaultEncoding: '', excludePattern: '', healthy: '', includePattern: '', pattern: '**/findbugsXml.xml', unHealthy: ''])

  step([$class: 'AnalysisPublisher', canComputeNew: false, defaultEncoding: '', healthy: '', unHealthy: ''])

}
