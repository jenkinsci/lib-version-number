pipeline {
    agent none
    tools {
        maven "mvn"
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
