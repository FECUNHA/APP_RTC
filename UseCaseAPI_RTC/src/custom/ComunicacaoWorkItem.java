package custom;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import connection.SysoutProgressMonitor;

import com.ibm.team.filesystem.common.workitems.ILinkConstants;
import com.ibm.team.links.client.ILinkManager;
import com.ibm.team.links.common.IItemReference;
import com.ibm.team.links.common.ILink;
import com.ibm.team.links.common.ILinkCollection;
import com.ibm.team.links.common.IReference;
import com.ibm.team.repository.client.IItemManager;
import com.ibm.team.repository.client.ILoginHandler2;
import com.ibm.team.repository.client.ILoginInfo2;
import com.ibm.team.repository.client.ITeamRepository;
import com.ibm.team.repository.client.TeamPlatform;
import com.ibm.team.repository.client.login.UsernameAndPasswordLoginInfo;
import com.ibm.team.repository.common.IItemHandle;
import com.ibm.team.repository.common.TeamRepositoryException;
import com.ibm.team.scm.client.IWorkspaceManager;
import com.ibm.team.scm.common.IChange;
import com.ibm.team.scm.common.IChangeSet;
import com.ibm.team.scm.common.IChangeSetHandle;
import com.ibm.team.scm.common.IVersionable;
import com.ibm.team.scm.common.IVersionableHandle;
import com.ibm.team.workitem.client.IWorkItemClient;
import com.ibm.team.workitem.client.IWorkItemWorkingCopyManager;
import com.ibm.team.workitem.client.WorkItemWorkingCopy;
import com.ibm.team.workitem.common.internal.model.WorkItemHandle;
import com.ibm.team.workitem.common.internal.workflow.WorkflowException;
import com.ibm.team.workitem.common.model.IAttribute;
import com.ibm.team.workitem.common.model.ITimeSheetEntry;
import com.ibm.team.workitem.common.model.ITimeSheetEntryHandle;
import com.ibm.team.workitem.common.model.IWorkItem;
import com.ibm.team.workitem.common.model.IWorkItemReferences;
import com.ibm.team.workitem.common.model.WorkItemEndPoints;

public class ComunicacaoWorkItem   {

	IWorkItem workItem = null;
	
	
	public IWorkItem getWorkItem() {
		return workItem;
	}

	public void setWorkItem(IWorkItem workItemClient) {
		this.workItem = workItemClient;
	}

	public void getChangeSetsByWorkItem(IWorkItem workitem, ITeamRepository repo) throws TeamRepositoryException {
		
		IWorkspaceManager workspaceManager = (IWorkspaceManager)repo.getClientLibrary(IWorkspaceManager.class);
		IItemManager itemManager = repo.itemManager();
		ILinkManager linkManager = (ILinkManager) repo.getClientLibrary(ILinkManager.class);
		
		IItemReference workItemReference = linkManager.referenceFactory().createReferenceToItem(workItem);
		ILinkCollection linkCollection = linkManager.findLinksByTarget(ILinkConstants.CHANGESET_WORKITEM_LINKTYPE_ID, workItemReference, new SysoutProgressMonitor()).getAllLinksFromHereOn();
		
		if (linkCollection.isEmpty()) {
			System.out.println("Work item has no change sets.");
			System.exit(0);
		}
		
		List<IChangeSetHandle> changeSetHandles = new ArrayList<IChangeSetHandle>();
		
		for (ILink link: linkCollection) {
			// Change set links should be item references
			IChangeSetHandle changeSetHandle = (IChangeSetHandle) link.getSourceRef().resolve();
			
			changeSetHandles.add(changeSetHandle);
		}
		
		@SuppressWarnings("unchecked")
		List<IChangeSet> changeSets = itemManager.fetchCompleteItems(changeSetHandles, IItemManager.DEFAULT, new SysoutProgressMonitor());
		Set<String> changedFilesAndFolders = new TreeSet<String>();
		for (IChangeSet cs: changeSets) {
			for (Object o: cs.changes()) {
				IChange change = (IChange)o;
				
				if (change.kind() != IChange.DELETE) {
					IVersionableHandle after = change.afterState();
					
					// Although versionable handles are item handles you cannot use the item
					//  manager to fetch the versionable from the handle. Instead, you use the
					//  versionable manager to do this.
					IVersionable afterVersionable = workspaceManager.versionableManager().fetchCompleteState(after, new SysoutProgressMonitor());

					changedFilesAndFolders.add(afterVersionable.getName());
				}
				
				// If there was a rename then include the old name in the list as well
				if (change.kind() == IChange.RENAME
						|| change.kind() == IChange.DELETE) {
					IVersionableHandle before = change.beforeState();
					
					IVersionable beforeVersionable = workspaceManager.versionableManager().fetchCompleteState(before, new SysoutProgressMonitor());
					
					changedFilesAndFolders.add(beforeVersionable.getName());
				}
			}
		}
		
		for (String fileOrFilderName: changedFilesAndFolders) {
			System.out.println("\t"+fileOrFilderName);
		}
	}
	
	public IWorkItem getWorkItemById(int workItemNumber, ITeamRepository repo) throws TeamRepositoryException,WorkflowException{
		// Gather the necessary clients and managers necessary from the repository
		// In this case we need the source control workspace manager and the item manager
		try{
			
		
		IWorkItemClient workItemClient = (IWorkItemClient)repo.getClientLibrary(IWorkItemClient.class);
		
		
		setWorkItem(workItemClient.findWorkItemById(workItemNumber, IWorkItem.FULL_PROFILE, new SysoutProgressMonitor()));
		
		WorkItemHandle handle = (WorkItemHandle) getWorkItem().getItemHandle(); 
		IWorkItemWorkingCopyManager wcm = workItemClient.getWorkItemWorkingCopyManager();
		
		wcm.connect(handle, IWorkItem.FULL_PROFILE, new SysoutProgressMonitor()); 
		
		WorkItemWorkingCopy wc = wcm.getWorkingCopy(handle);
		
		//IWorkItem copiedWorkItem = wc.getWorkItem(); 

        IWorkItemReferences workItemReferences = wc.getReferences();
        
        List<IReference> listReferences = workItemReferences.getReferences(WorkItemEndPoints.WORK_TIME);	
        
  	

        for (IReference reference : listReferences) {


            if (reference.isItemReference()) {


                IItemHandle iReferenceh = ((IItemReference) reference).getReferencedItem();


               if (iReferenceh instanceof ITimeSheetEntryHandle) {

                   ITimeSheetEntry newEntryTemp =  (ITimeSheetEntry) repo.itemManager().fetchCompleteItem(iReferenceh, IItemManager.DEFAULT, null);
                   
                   System.out.println(newEntryTemp.getStateId().getUuidValue());
                   
                   System.out.println(newEntryTemp.getItemId().getUuidValue());
                   
                   System.out.println(newEntryTemp.getTimeCodeId());
                   
                   System.out.println(newEntryTemp.getHoursSpent());

                 
                }

            }

        }
        


		for (int j = 0; j < wc.getWorkItem().getCustomAttributes().size(); j++) {
			
			com.ibm.team.workitem.common.model.IAttributeHandle handle1 = wc.getWorkItem().getCustomAttributes().get(j);
			IAttribute attribute = (IAttribute) repo.itemManager().fetchCompleteItem(handle1, IItemManager.DEFAULT, new SysoutProgressMonitor());
			System.out.println("The attribute id: " + attribute.getIdentifier());
			System.out.println("The attribute name: " + attribute.getDisplayName());
			System.out.println("The value of the custom attribute: " + workItem.getValue(attribute));
			
			
		}
		
		}catch (WorkflowException e) {
			throw e;
		}
		
		catch (TeamRepositoryException e) {
			throw e;
		}
			

//		copiedWorkItem.setHTMLSummary(XMLString.createFromPlainText("Projeto Retenção Inteligente - editado API"));
//
//		IAttribute stateAttribute = workItemClient.findAttribute( copiedWorkItem.getProjectArea(), IWorkItem.STATE_PROPERTY, new SysoutProgressMonitor());
//
//		copiedWorkItem.setValue(stateAttribute, null); 
//
//		
//
//		wc.save(new SysoutProgressMonitor()); 
//		
		
		return this.getWorkItem();
		
	}
	
	
	
	
	public String toString(){
		return ("["+getWorkItem().getId()+"] "+getWorkItem().getHTMLSummary().getPlainText());
	}
	
	public void getAll(){
		String repoUri = "https://jazz.net/sandbox01-ccm/web";
	
		final String userId = "user"; // Retrieve the userId in a secure way
		final String password = "pass"; // Retrieve the password in a
	
		try {
			TeamPlatform.startup();
			
			ITeamRepository repo = TeamPlatform.getTeamRepositoryService().getTeamRepository(repoUri);
			
			repo.registerLoginHandler(new ILoginHandler2() {
				@Override
				public ILoginInfo2 challenge(ITeamRepository repo) {
					return new UsernameAndPasswordLoginInfo(userId, password);
				}
			});
	
			
			/* Do all of my work with myserver here. */
			
			
			repo.login(new SysoutProgressMonitor());

			
			// Gather the necessary clients and managers necessary from the repository
			// In this case we need the source control workspace manager and the item manager
			IWorkspaceManager workspaceManager = (IWorkspaceManager)repo.getClientLibrary(IWorkspaceManager.class);
			IItemManager itemManager = repo.itemManager();
			IWorkItemClient workItemClient = (IWorkItemClient)repo.getClientLibrary(IWorkItemClient.class);
			ILinkManager linkManager = (ILinkManager) repo.getClientLibrary(ILinkManager.class);
			
			// Find the work item on the server
			int workItemNumber = -1;
			
			try {
				workItemNumber = Integer.parseInt("0"); //adicionar valor workitem
			} catch (NumberFormatException e) {
				System.out.println("Invalid work item nubmer:"+"0");//adicionar valor workitem
				System.exit(1);
			}
			
			IWorkItem workItem = workItemClient.findWorkItemById(workItemNumber, IWorkItem.FULL_PROFILE, new SysoutProgressMonitor());
			
			System.out.println("["+workItem.getId()+"] "+workItem.getHTMLSummary().getPlainText());
				
			
			// Find all of the attached change sets using the link manager to find a special kind of
			//  link that crosses between work items and source control change sets using its ID.
			IItemReference workItemReference = linkManager.referenceFactory().createReferenceToItem(workItem);
			ILinkCollection linkCollection = linkManager.findLinksByTarget(ILinkConstants.CHANGESET_WORKITEM_LINKTYPE_ID, workItemReference, new SysoutProgressMonitor()).getAllLinksFromHereOn();
			
			if (linkCollection.isEmpty()) {
				System.out.println("Work item has no change sets.");
				System.exit(0);
			}
			
			List<IChangeSetHandle> changeSetHandles = new ArrayList<IChangeSetHandle>();
			
			for (ILink link: linkCollection) {
				// Change set links should be item references
				IChangeSetHandle changeSetHandle = (IChangeSetHandle) link.getSourceRef().resolve();
				
				changeSetHandles.add(changeSetHandle);
			}
			
			@SuppressWarnings("unchecked")
			List<IChangeSet> changeSets = itemManager.fetchCompleteItems(changeSetHandles, IItemManager.DEFAULT, new SysoutProgressMonitor());
			Set<String> changedFilesAndFolders = new TreeSet<String>();
			for (IChangeSet cs: changeSets) {
				for (Object o: cs.changes()) {
					IChange change = (IChange)o;
					
					if (change.kind() != IChange.DELETE) {
						IVersionableHandle after = change.afterState();
						
						// Although versionable handles are item handles you cannot use the item
						//  manager to fetch the versionable from the handle. Instead, you use the
						//  versionable manager to do this.
						IVersionable afterVersionable = workspaceManager.versionableManager().fetchCompleteState(after, new SysoutProgressMonitor());
	
						changedFilesAndFolders.add(afterVersionable.getName());
					}
					
					// If there was a rename then include the old name in the list as well
					if (change.kind() == IChange.RENAME
							|| change.kind() == IChange.DELETE) {
						IVersionableHandle before = change.beforeState();
						
						IVersionable beforeVersionable = workspaceManager.versionableManager().fetchCompleteState(before, new SysoutProgressMonitor());
						
						changedFilesAndFolders.add(beforeVersionable.getName());
					}
				}
			}
			
			for (String fileOrFilderName: changedFilesAndFolders) {
				System.out.println("\t"+fileOrFilderName);
			}
		
		} catch (TeamRepositoryException e) {
			 System.out.println(e.getStackTrace());
		} finally {
			TeamPlatform.shutdown();
		}
		
	}
}
