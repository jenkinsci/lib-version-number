pipeline {
    agent none
    tools {
        maven "mvn"
        jdk 'jdk8'
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
