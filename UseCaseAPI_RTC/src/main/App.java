package main;
import com.ibm.team.repository.client.ITeamRepository;
import com.ibm.team.repository.common.TeamRepositoryException;
import com.ibm.team.workitem.common.internal.workflow.WorkflowException;
import com.ibm.team.workitem.common.model.IWorkItem;

import connection.Conexao;
import custom.ComunicacaoWorkItem;


public class App {

		public static void main(String[] args) {
			
			Conexao conn = null;
			int workItemNumber = 0;
			
			try {
				
				// Find the work item on the server
				if(args != null){
					workItemNumber = Integer.parseInt(args[0]);
				}
				
//			try {
//				workItemNumber = 17122;
//			}
				
			}catch (NumberFormatException e) {
					System.out.println("Invalid work item nubmer:"+args[3]);
					System.exit(1);
				}
		
			try {
				
				conn = Conexao.getInstance();
				ITeamRepository repo = conn.getConnection();
				
				ComunicacaoWorkItem workItemC =  new ComunicacaoWorkItem();
				
				IWorkItem workItem = workItemC.getWorkItemById(workItemNumber, repo);
				///IWorkItem workItem = workItemC.getTasks(repo);
				
				
				System.out.println("["+workItem.getId()+"] "+workItem.getHTMLSummary().getPlainText());
				
				workItemC.getChangeSetsByWorkItem(workItem, repo);
				
			} catch (TeamRepositoryException e) {
				 System.out.println(e.getStackTrace());
			} finally {
				try {
					conn.closeConn();
				} catch (TeamRepositoryException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			
		}
}
