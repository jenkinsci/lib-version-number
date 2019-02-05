pipeline {
    agent none
    tools {
        maven "mvn"
        jdk 'jdk8'
    }
    options {
        buildDiscarder(logRotator(numToKeepStr: '10'))
    }
    stages {
        stage('Parallel Stage') {
            failFast true
            parallel {
                stage('Linux') {
                    agent {
                        label "linux"
                    }
                    steps {
                        sh 'mvn -V -B clean verify'
                    }
                }
                stage('Windows') {
                    agent {
                        label "windows"
                    }
                    steps {
                        bat 'mvn -V -B clean verify'
                    }
                }
            }
        }
    }
}
