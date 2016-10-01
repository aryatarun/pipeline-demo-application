stage 'Commit-Stage'


node {
  git url: 'git@bitbucket.org:thomasanderer/pipeline-demo.git'


  def mvnHome = tool 'M3'
  sh "${mvnHome}/bin/mvn -B verify"
}
