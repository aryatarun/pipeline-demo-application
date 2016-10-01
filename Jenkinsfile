stage 'Commit-Stage'


node {
git url: 'https://github.com/cloudfoundry-samples/pong_matcher_spring.git'


def mvnHome = tool 'M3'
sh "${mvnHome}/bin/mvn -B verify"
}