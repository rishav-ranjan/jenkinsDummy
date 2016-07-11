import groovy.json.JsonBuilder
import groovy.json.JsonSlurper

//incoming parameters
//def url = "https://github.com/WPPg2/DevOps-Deployment"
//def gitCredentials = "8cf0000b-3991-4db0-a2d9-e157168d2cef"
//def serviceName="avreg"
//def serviceConfigPath = "/home/ec2-user/avregPipPilot/pipeline/services"
//def amiID = "ami-0a6f966a"
//def subnetNum
//def environment="pie"

def content
def content2
def slaveWorkspaceDir
def masterWorkspace
def subnet = "subnet${subnetNum}"
def jsonInputFile
def jsonWaitFile
def jsonDetailsFile
node(targetNode)
{
	//get slave workspace directory
    slaveWorkspaceDir = pwd()
}
node('master'){
	masterWorkspace = pwd()
	getConfigFile(serviceConfigBaseURL,"environment-config.json")
	//get values from environment config file
	File inputFile1 = new File("${masterWorkspace}/environment-config.json")
	content=inputFile1.text
	def slurped = new JsonSlurper().parseText(content)
	jsonInputFile = slurped[ environment ][ subnet ].input
	jsonWaitFile = slurped[ environment ][ subnet ].wait
	jsonDetailsFile = slurped[ environment ][ subnet ].details
	
	getConfigFile(serviceConfigBaseURL,"${jsonInputFile}")
    //manipulate json file 1
    File inputFile = new File("${masterWorkspace}/${jsonInputFile}")
    content = inputFile.text
    slurped = new JsonSlurper().parseText(content)
    def builder = new JsonBuilder(slurped)
    builder.content.stack.parameters[2].value = "${amiID}"
    content = builder.toPrettyString()
	
    // null since non-serializable
    builder=null
}

node(targetNode) {
	//git checkout before any writeFile
    git credentialsId: "${gitCredentials}", url: "${gitDeployURL}" 
	
    //writing manipulated json to slave node
    writeFile file: "${jsonInputFile}", text: content
    }
node('master'){
    //fetching json file 2
	getConfigFile(serviceConfigBaseURL,"${jsonWaitFile}")
	stash includes: "${jsonWaitFile}", name: "${jsonWaitFile}"
}

node(targetNode){
    //writing json to slave node
	unstash "${jsonWaitFile}"
    }
node('master'){
    //fetching json file 3
	getConfigFile(serviceConfigBaseURL,"${jsonDetailsFile}")
	stash includes: "${jsonDetailsFile}", name: "${jsonDetailsFile}"
}


node(targetNode){
	//writing json to slave node
    unstash "${jsonDetailsFile}"  
    println "Deploying ${serviceName} on ${environment} stack. POD1-${subnet} with ami_id: ${amiID}"
	/*sh """cd ${slaveWorkspaceDir}/chef-repos/aws-manager-repo
    sudo chef-client -z -w -j ${slaveWorkspaceDir}/${jsonInputFile} -r 'recipe[cloudformation::updateStack]' -l info
    sudo chef-client -z -w -j ${slaveWorkspaceDir}/${jsonWaitFile}  -r 'recipe[cloudformation::waitForStackReady]' -l info
    sudo chef-client -z -w -j ${slaveWorkspaceDir}/${jsonDetailsFile} -r 'recipe[cloudformation::getStackOutputs]' -l info
    """*/
    def amiContent = "ami_id=${amiID}"
    writeFile file: "${serviceName}.log.amiid.tag", text: amiContent
}


def getConfigFile(baseURL,fileName) {
    def workspace = pwd()
    sh """mkdir -p "${workspace}"
	"""
    def file = new File("${workspace}/${fileName}").newOutputStream()  
    file << new URL("${baseURL}/${fileName}").openStream()  
    file.close()
}