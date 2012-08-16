/**
 * 
 */
package net.sourceforge.texlipse.filefinder;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;

/**
 * Class that allows project files according to their absolute path.
 * 
 * @author nbraun
 *
 */
public class ProjectFileFinder {

    /**
     * Implementation of a IResourceVisitor for finding files in the project
     * according to an absolute file system path.
     * 
     * @author nbraun
     */
    private static class FileFinder implements IResourceVisitor
    {
    	/** The path to look for */
    	String pathToFind;
    	/** The file that will be returned */
    	IFile file;

    	/**
    	 * Construct a new project file finder to be used as resource visitor.
    	 *
    	 * @param pathToFind the path and name of the file to find. The path
    	 *                   separators must be the '/' character since the
    	 *                   resource path's are denoted as such.
    	 *                   Secondly it must contain the file name of the
    	 *                   file to find. All files in the project including
    	 *                   also files to linked resources will then be tested
    	 *                   whether the file resource name ends with this
    	 *                   file path.
    	 *
    	 * @param currentFile The current file or null if omitted. If no file
    	 *                    is found, the value passed here will be returned 
    	 *                    in getFile().
    	 */
    	public FileFinder(String pathToFind, IFile currentFile) {
			this.pathToFind = pathToFind;
			this.file = currentFile;
		}

    	/**
    	 * This visitor checks each file whether it ends with the string
    	 * passed in the constructor with the argument pathToFind.
    	 *
    	 * @return false, when a match was found. true otherwise to keep
    	 *         searching
    	 *
    	 * @see IResourceVisitor.visit
    	 */
		public boolean visit(IResource resource) throws CoreException {
			/** Resource must be of type IFile */
			try
			{
				IFile projFile = (IFile)resource;

				/** 
				 * Compare the location of the current file with the absolute
				 * path that was set during initialization.
				 */
				if (projFile.getLocation().toFile().getAbsolutePath().compareTo(pathToFind) == 0)
				{
					/** This is the file to be returned */
					file = projFile;

					/** File is found, no need to look further */
					return false;
				}
			}catch (ClassCastException e){}

			/** No match found, therefore keep visiting */
			return true;
		}

		/**
		 * Get the file that matches the pattern.
		 *
		 * @return Returns the file that was found or the file that was set when the
		 *         object was created
		 */
		IFile getFile()
		{
			return file;
		}
    }
	
	/**
	 * Find a file in the project according to its absolute path.
	 * 
	 * Often the binaries return an absolute path rather than a project relative
	 * path. In addition, if resources in the project are linked, in this case
	 * it can not easily be linked to the file.
	 * 
	 * This method searches the project and checks each file whether its 
	 * absolute location matches the path. The first match will be returned.
	 * 
	 * @param project The project to look up the files.
	 * @param absoluteFilePath The absolute file path that must match. Every
	 *                         file in the project will be checked against
	 *                         this path.
	 * @return The IFile that matches the absoluteFilePath or null, if no
	 *         file matches.
	 */
	public static IFile findFile(IProject project, String absoluteFilePath)
	{
		/** 
		 * Create a new file finder. Set the path and the default file
		 * to return to null, if no file was found.
		 */
		FileFinder visitor = new FileFinder(absoluteFilePath, null);
		
		try {
			/** Visit all resources */
			project.accept(visitor);
		} catch (CoreException e) {
			e.printStackTrace();
		}
		
		/** Return what has been found */
		return visitor.getFile();
	}
}
