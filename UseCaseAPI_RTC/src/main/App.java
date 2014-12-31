package main;
import custom.ComunicacaoWorkItem;
import connection.Conexao;
import com.ibm.team.repository.client.ITeamRepository;
import com.ibm.team.repository.common.TeamRepositoryException;
import com.ibm.team.workitem.common.model.IWorkItem;


public class App {

		public static void main(String[] args) throws TeamRepositoryException {
			
			Conexao conn = null;
			
			try {
				
				// Find the work item on the server
				int workItemNumber = -1;
				
				try {
					workItemNumber = 17122;
				} catch (NumberFormatException e) {
					System.out.println("Invalid work item nubmer:"+args[3]);
					System.exit(1);
				}
				
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
				conn.closeConn();
			}
			
			
		}
}
