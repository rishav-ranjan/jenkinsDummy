// Params to this Job
//serviceConfigBaseURL  eg  "file:///home/ec2-user/config/avreg"
//targetNode  eg  "MicroLoadTest"

/*
node('master') {
   getConfigFile(serviceConfigBaseURL,"build.xml")
    getConfigFile(serviceConfigBaseURL,"cho-properties.xml")
    getConfigFile(serviceConfigBaseURL,"loadTest.properties")
    getConfigFile(serviceConfigBaseURL,"default-properties.xml")

    stash includes: 'build.xml', name: 'build.xml'
    stash includes: 'cho-properties.xml', name: 'cho-properties.xml'
    stash includes: 'loadTest.properties', name: 'loadTest.properties'
    stash includes: 'default-properties.xml', name: 'default-properties.xml'
}
*/

node(targetNode) {
   /* unstash 'build.xml'
    unstash 'cho-properties.xml'
    unstash 'loadTest.properties'
    unstash 'default-properties.xml'
       */         
    //ant
    sh "cd /home/ec2-user/workspace/MicroLoadTestPilot2
        nohup ant &"
}

def getConfigFile(baseURL,fileName) {
    def workspace = pwd()
    def file = new File("${workspace}/${fileName}").newOutputStream()  
    file << new URL("${baseURL}/${fileName}").openStream()  
    file.close()
}
