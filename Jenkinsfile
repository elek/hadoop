pipeline {
    agent { docker 'elek/ozone-build' } 
    stages {
        stage('Build') {
            steps {
                pullRequest.createStatus(status: 'pending',
                         context: 'continuous-integration/jenkins/pr-merge/build',
                         description: 'All tests are passing')
                sh 'mvn clean install -DskipTests -DskipShade -Pdist,hdds -Dmaven.javadoc.skip=true -am -pl :hadoop-ozone-dist'
                pullRequest.createStatus(status: 'success',
                         context: 'continuous-integration/jenkins/pr-merge/build',
                         description: 'Project is build without any error')
            }
        }
    }
}
