import os


class DataProcessing:

    @staticmethod
    def string_to_list(input_string):
    # Remove the square brackets from the string
        input_string = input_string.strip('[]')

        # Split the string by commas and convert each element to a floating-point number
        list_elements = input_string.split(',')
        float_list = [float(element.strip()) for element in list_elements]

        return float_list

    @staticmethod
    def create_relative_path(relativePathWanted):
        relative_path = f"{relativePathWanted}"  # Change this to your desired relative path

        current_dir = os.path.dirname(os.path.abspath(__file__))

        # Navigate up two levels to the "Plot Service (Python)" folder
        project_root = os.path.dirname(os.path.dirname(current_dir))

        # Construct the full path to save the figure
        full_path = os.path.join(project_root, relative_path)

        # Create the directory if it doesn't exist
        os.makedirs(os.path.dirname(full_path), exist_ok=True)
        return full_path