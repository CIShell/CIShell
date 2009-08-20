#include <exception>
#include <iostream>
#include <fstream>


#define PROGRAM_NAME "NonJavaAlgorithmWizardExample"
#define EXPECTED_ARGUMENT_COUNT 4
#define USER_FILE_ARGUMENT_INDEX 1
#define PLATFORM_FILE_ARGUMENT_INDEX 2
#define OUTPUT_FILE_ARGUMENT_INDEX 3

std::string readUserFile(const std::string& userFileName)
	throw(std::exception);
std::string readPlatformFile(const std::string& platformFileName)
		throw(std::exception);
std::string readFileContents(std::ifstream& inputFileStream);

void outputCombinedContentsToFile(const std::string& userFileContents,
								  const std::string& platformFileContents,
								  const std::string& fileName)
	throw(std::exception);

int main (int argumentCount, char* arguments[]) {
	if (argumentCount < EXPECTED_ARGUMENT_COUNT) {
		std::cerr << "You must provide " << EXPECTED_ARGUMENT_COUNT;
		std::cerr << " arguments to the program." << std::endl;
		std::cerr << "Expected format:" << std::endl;
		std::cerr << "user_file platform_file output_file" << std::endl;
	} else {
		// Process the user-specified file.

		std::string userFileName = arguments[USER_FILE_ARGUMENT_INDEX];
		std::string userFileContents;
		
		try {
			userFileContents = readUserFile(userFileName);
			
			std::cout << "Successfully read the file you specified \'";
			std::cout << userFileName << "\'." << std::endl;
		} catch (std::exception& readUserFileException) {
			std::cerr << "There was an error reading your file \'";
			std::cerr << userFileName << "\': \"";
			std::cerr << readUserFileException.what() << "\"" << std::endl;
			
			return 1;
		}
		
		// Process the platform file.
		
		std::string platformFileName = arguments[PLATFORM_FILE_ARGUMENT_INDEX];
		std::string platformFileContents;
		
		try {
			platformFileContents = readPlatformFile(platformFileName);
			
			std::cout << "Successfully read the platform file \'";
			std::cout << platformFileName << "\'." << std::endl;
		} catch (std::exception& readPlatformFileException) {
			std::cerr << "There was an error reading the platform file \'";
			std::cerr << platformFileName << "\': \"";
			std::cerr << readPlatformFileException.what() << "\"" << std::endl;
			
			return 1;
		}
		
		/*
		 * Combine the user-specified file contents and the platform file into
		 *  the user-specified output file.
		 */
		
		std::string outputFileName = arguments[OUTPUT_FILE_ARGUMENT_INDEX];
		
		try {
			outputCombinedContentsToFile(
				userFileContents, platformFileContents, outputFileName);
			
			std::cout << "Successfully wrote the combined contents to the ";
			std::cout << "file \'" << outputFileName << "\'." << std::endl;
		} catch (std::exception& outputCombinedContentsToFileException) {
			std::cerr << "There was an error outputting the combined contents";
			std::cerr << " of the file you specified and the platform file ";
			std::cerr << "to the file \'" << outputFileName << "\': \"";
			std::cerr << outputCombinedContentsToFileException.what();
			std::cerr << "\"" << std::endl;
		}
	}
	
	return 0;
}

std::string readUserFile(const std::string& userFileName)
		throw(std::exception) {
	std::ifstream userFileStream(userFileName.c_str(), std::ifstream::in);
	
	if (!userFileStream.is_open()) {
		throw std::ios_base::failure("Unable to open user file for reading.");
	}
	
	std::string userFileContents = readFileContents(userFileStream);
	userFileStream.close();
	
	return userFileContents;
}

std::string readPlatformFile(const std::string& platformFileName)
		throw(std::exception) {
	std::ifstream platformFileStream(platformFileName.c_str(),
									 std::ifstream::in);
	
	if (!platformFileStream.is_open()) {
		throw std::ios_base::failure(
			"Unable to open platform file for reading.");
	}
	
	std::string platformFileContents = readFileContents(platformFileStream);
	platformFileStream.close();
	
	return platformFileContents;
}

std::string readFileContents(std::ifstream& inputFileStream) {
	std::string fileContents;
	
	while (inputFileStream.good()) {
		fileContents += inputFileStream.get();
	}
	
	return fileContents;
}

void outputCombinedContentsToFile(const std::string& userFileContents,
								  const std::string& platformFileContents,
								  const std::string& fileName)
		throw(std::exception) {
	std::ofstream outputFileStream(fileName.c_str(), std::ofstream::out);
	
	if (!outputFileStream.is_open()) {
		throw std::ios_base::failure(
			"Unable to open output file for writing.");
	}
	
	outputFileStream << userFileContents << std::endl;
	outputFileStream << platformFileContents << std::endl;
}
