pipeline {
    agent { docker 'elek/ozone-build' } 
    stages {
        stage('Build') {
            steps {
                script {
                   pullRequest.createStatus(status: 'pending',
                         context: 'continuous-integration/jenkins/pr-merge/build',
                         description: 'Maven build has been started')
                }
                sh 'mvn clean install -DskipTests -DskipShade -Pdist,hdds -Dmaven.javadoc.skip=true -am -pl :hadoop-ozone-dist'
            }
            post {
               failure {
                    script {
                       pullRequest.createStatus(status: 'error',
                         context: 'continuous-integration/jenkins/pr-merge/build',
                         description: 'Maven build is failed')
                    }
               }
               unstable {
                    script  {
                       pullRequest.createStatus(status: 'error',
                         context: 'continuous-integration/jenkins/pr-merge/build',
                         description: 'Maven build is unstable')
                    }
               }
               success {
                    script {
                       pullRequest.createStatus(status: 'success',
                         context: 'continuous-integration/jenkins/pr-merge/build',
                         description: 'Maven build is OK')
                    }
               }
            }
        }

 stage('Checks') {
 paralel {
         stage('Unit tests') {
                    steps {
                        script {
                           pullRequest.createStatus(status: 'pending',
                                 context: 'continuous-integration/jenkins/pr-merge/unit',
                                 description: 'Maven build has been started')
                        }
                        sh 'mvn test -fae -DskipShade -Pdist,hdds -Dmaven.javadoc.skip=true -am -pl :hadoop-ozone-dist'
                    }
                    post {
                       failure {
                            script {
                               pullRequest.createStatus(status: 'error',
                                 context: 'continuous-integration/jenkins/pr-merge/unit',
                                 description: 'Unit tests are failed.')
                            }
                       }
                       unstable {
                            script {
                               pullRequest.createStatus(status: 'error',
                                 context: 'continuous-integration/jenkins/pr-merge/unit',
                                 description: 'Unit tests are failed.')
                            }
                       }
                       success {
                            script {
                               pullRequest.createStatus(status: 'success',
                                 context: 'continuous-integration/jenkins/pr-merge/unit',
                                 description: 'Unit tests are passed')
                            }
                       }
                    }

         }


         stage('Checkstyle') {
                    steps {
                        script {
                           pullRequest.createStatus(status: 'pending',
                                 context: 'continuous-integration/jenkins/pr-merge/checkstyle',
                                 description: 'Checkstyle run has been started')
                        }
                        sh 'mvn checkstyle:check -f hadoop-hdds/pom.xml'
                        sh 'mvn checkstyle:check -f hadoop-ozone/pom.xml'

                    }
                    post {
                       failure {
                            script {
                               pullRequest.createStatus(status: 'error',
                                 context: 'continuous-integration/jenkins/pr-merge/checkstyle',
                                 description: 'Unit tests are failed.')
                            }
                       }
                       unstable {
                            script {
                               pullRequest.createStatus(status: 'error',
                                 context: 'continuous-integration/jenkins/pr-merge/checkstyle',
                                 description: 'Checkstyle tests are failed.')
                            }
                       }
                       success {
                            script {
                               pullRequest.createStatus(status: 'success',
                                 context: 'continuous-integration/jenkins/pr-merge/checkstyle',
                                 description: 'Checkstyle tests are passed')
                            }
                       }
                    }

         }
    }
}
}
}
