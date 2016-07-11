import groovy.json.JsonBuilder
import groovy.json.JsonSlurper

//incoming parameters
//def url = "https://github.com/WPPg2/DevOps-Deployment"
//def gitCredentials = "8cf0000b-3991-4db0-a2d9-e157168d2cef"
//def serviceName="avreg"
//def serviceConfigBaseURL
//def targetNode = 'AMIBuilder'
//def cookbookVersion="1.0.16"
//def artifactVersion

def key
def secret_file
def content
def logName
def temp_dir
def SlaveWorkspaceDir
def masterWorkspace
def instanceID


node('master'){
	masterWorkspace = pwd()
	getConfigFile(serviceConfigBaseURL,"deploy_input.json")
    //manipulate json file 1
    File inputFile = new File("${masterWorkspace}/deploy_input.json")
    content = inputFile.text
    def slurped = new JsonSlurper().parseText(content)
    secret_file = slurped.secretFile
	def artifactURL = slurped.service.artifact_url
	artifactURL=artifactURL.replaceAll(~/POMVERSION/, "${artifactVersion}")
    println "Secret file is ${secret_file}"
    def builder = new JsonBuilder(slurped)
    builder.content.service.artifact_url = "${artifactURL}"
    content = builder.toPrettyString()
	
    // null since non-serializable
    builder=null
}
    
node(targetNode){
    //git checkout before any writeFile
    git credentialsId: "$gitCredentials", url: "$url"
			
    // get slave workspace dir
    slaveWorkspaceDir = pwd()
			
	//write manipulated json to slave
    writeFile file: "deploy_input.json", text: content
}

node('master'){
	getConfigFile(serviceConfigBaseURL,"deploy.json")
	//manipulate json file 2
	File inputFile2 = new File("${masterWorkspace}/deploy.json")
    content = inputFile2.text
    def slurped2 = new JsonSlurper().parseText(content)
    key=slurped2.aws.instance.key.local_path
	def inputURL=slurped2.service.cookbook.url
	inputURL=inputURL.replaceAll(~/COOKBOOKVERSION/, "${cookbookVersion}")
	def databagURL=slurped2.service.env_databag_url
	databagURL=databagURL.replaceAll(~/COOKBOOKVERSION/, "${cookbookVersion}")
    println "AWS key is ${key}"
    logName=slurped2.log.name
    tempDir=slurped2.log.temp_dir
    println "logName is ${logName}"
	def builder = new JsonBuilder(slurped2)
	builder.content.service.cookbook.url="${inputURL}"
	builder.content.service.env_databag_url="${databagURL}"
	content = builder.toPrettyString()
}

node (targetNode){
	//write manipulated json to slave
    writeFile file: "deploy.json", text: content
	
	//run deploy service recipe
    sh """ cd ${slaveWorkspaceDir}/chef-repos/aws-manager-repo/cookbooks
           sudo chef-client -z -j ${slaveWorkspaceDir}/deploy.json -r 'recipe[ec2::deployService]' --log_level info
       """
	   
    //get deployment IP
    def ip_ad = readFile file: "${tempDir}/${logName}.ipaddr"
    def ip_addr =ip_ad.trim()
    println "the IP address is ${ip_addr}"
	
    //get instance id
    def inst_id = readFile file: "${tempDir}/${logName}.instid"
    instanceID = inst_id.trim()
    println "${serviceName} Instance ID is ${instanceID}"
	
	//secure copy databag
    sh "sudo scp -q -o StrictHostKeyChecking=no  -i ${key} /opt/keyfiles/stage/${serviceName}/encrypted_data_bag_secret ec2-user@${ip_addr}:${secret_file}"
}

//outgoing parameters- instanceID
currentBuild.setDescription("#instanceID="+instanceID)

def getConfigFile(baseURL,fileName) {
    def workspace = pwd()
    sh """mkdir -p "${workspace}"
	"""
    def file = new File("${workspace}/${fileName}").newOutputStream()  
    file << new URL("${baseURL}/${fileName}").openStream()  
    file.close()
}
