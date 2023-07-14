# Bayesian Network Project

This project involved building a Bayesian network from scratch in Java. The goal was to create a program that could construct a dependency graph based on a provided XML file, which specified the relationships between objects and their probabilities. The program also handled conditional dependencies and optimized computational time by utilizing conditional dependency techniques. 

## Technologies Used

- Java
- No standard packages were allowed and everything was built from scratch

## Project Overview

The project consisted of the following components:

1. **Node**: Implemented a Node class to represent individual nodes in the Bayesian network. Each node contained information such as its name, parents, children, and conditional probability tables (CPTs).

2. **CreateGraph**: Created a CreateGraph class responsible for parsing the XML file and constructing the dependency graph based on the provided information. This class handled reading the XML file, creating nodes, and establishing parent-child relationships.

3. **ConditionalProbabilityTables (CPTs)**: Developed the CPTs class to store and manage the conditional probability tables associated with each node. The CPTs class handled probability calculations and data storage required for the Bayesian network.

Functionality
The program followed the following workflow:

Input Files: Create an input.txt file with the following structure:

The first line should specify the path to the XML file. You can use one of the provided XML files or provide your own, as long as it follows the same format.
Below the first line, write queries using the format P(<expression>). Use =T for true, =F for false, and | to denote conditional dependencies.
After each query, specify the method to be used: ,1 for full probability calculation or ,2 for conditional probability calculation.
Dependency Graph Construction: Run the program and provide the input.txt file as input.

The program will read the XML file specified in the first line of the input.txt file and construct the dependency graph based on the provided information.
Query Processing: The program will process the queries specified in the input.txt file and calculate the probabilities using the specified method.

The output will include the result of each query and the number of additions and multiplications performed during the calculation.
Usage
To use the program, follow these steps:

Create an input.txt file with the following structure:

<path_to_xml_file>
<query_1>,<method_1>
<query_2>,<method_2>
<query_3>,<method_3>
...
Replace <path_to_xml_file> with the path to the XML file.
Specify each query using the format P(<expression>).
Use =T for true, =F for false, and | to denote conditional dependencies.
After each query, specify the method to be used: ,1 for full probability calculation or ,2 for conditional probability calculation.
Run the program and provide the input.txt file as input.
  
 ### OR use the current inputfile and xml files 
  
The program will read the XML file and construct the dependency graph.
It will process the queries and provide the results, along with the number of additions and multiplications performed for each query.
Conclusion
This Bayesian network project successfully implemented a Java program capable of constructing a dependency graph from an XML file and processing queries. The program showcased the handling of conditional dependencies, string slicing techniques, and the construction of truth tables and conditional probability tables. By efficiently utilizing these techniques, the program provided accurate answers and optimized computational time.

Please note that the project details and instructions are provided as a general outline. You can modify and customize the information according to your specific project requirements and specifications.
This Bayesian network project successfully implemented a Java program capable of constructing a dependency graph from an XML file and processing queries. The program showcased the handling of conditional dependencies, string slicing techniques, and the construction of truth tables and conditional probability tables. By efficiently utilizing these techniques, the program provided accurate answers and optimized computational time.

Please note that the project details and instructions are provided as a general outline. You can modify and customize the information according to your specific project requirements and specifications.
