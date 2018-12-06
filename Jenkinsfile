pipeline {
    agent { docker 'elek/ozone-build' } 
    stages {
        stage('Build') {
            steps {
                sh 'mvn clean install -DskipTests -DskipShade -Pdist,hdds -Dmaven.javadoc.skip=true -am -pl :hadoop-ozone-dist'
            }
        }
    }
}
