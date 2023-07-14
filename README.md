# Bayesian Network Project

This project involved building a Bayesian network from scratch in Java. The goal was to create a program that could construct a dependency graph based on a provided XML file, which specified the relationships between objects and their probabilities. The program also handled conditional dependencies and optimized computational time by utilizing conditional dependency techniques. 

## Technologies Used

- Java
- bayesball
- probabilitys
- No standard packages were allowed and everything was built from scratch

## Project Overview

The project consisted of the following components:

1. **Node**: Implemented a Node class to represent individual nodes in the Bayesian network. Each node contained information such as its name, parents, children, and conditional probability tables (CPTs).

2. **CreateGraph**: Created a CreateGraph class responsible for parsing the XML file and constructing the dependency graph based on the provided information. This class handled reading the XML file, creating nodes, and establishing parent-child relationships.

3. **ConditionalProbabilityTables (CPTs)**: Developed the CPTs class to store and manage the conditional probability tables associated with each node. The CPTs class handled probability calculations and data storage required for the Bayesian network.

## Functionality

The program followed the following workflow:

1. **XML and Text Input**: Received an XML file specifying the relationships between objects and their probabilities. Additionally, a text file containing queries was provided to obtain the desired results.

2. **Dependency Graph Construction**: Utilized the CreateGraph class to parse the XML file and construct the dependency graph. The graph represented the relationships between the objects and their conditional dependencies.

3. **Query Processing**: Read the queries from the text file and utilized string slicing techniques to extract relevant information. Built truth tables and CPTs to calculate the probabilities and provide the correct answers.

4. **Output**: Generated the appropriate output based on the queries, presenting the calculated probabilities and results for the given dependencies.

## Usage

To use the program, follow these steps:

1. Prepare an XML file that specifies the relationships between objects and their probabilities.
2. Create a text file containing the queries to be processed.
3. Run the program, providing the XML file and the text file as inputs.
4. The program will construct the dependency graph, process the queries, and output the results.

## Conclusion

This Bayesian network project successfully implemented a Java program capable of constructing a dependency graph from an XML file and processing queries. The program showcased the handling of conditional dependencies, string slicing techniques, and the construction of truth tables and conditional probability tables. By efficiently utilizing these techniques, the program provided accurate answers and optimized computational time.

Please note that the project details and instructions are provided as a general outline. You can modify and customize the information according to your specific project requirements and specifications.
