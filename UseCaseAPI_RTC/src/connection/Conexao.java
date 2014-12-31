package connection;

import com.ibm.team.repository.client.ITeamRepository;
import com.ibm.team.repository.client.TeamPlatform;
import com.ibm.team.repository.common.TeamRepositoryException;

public class Conexao {

	private static Conexao instance = null;

	public Conexao() {

	}

	public static Conexao getInstance() {
		if (instance == null) {
			instance = new Conexao();
		}
		return instance;
	}

	public ITeamRepository getConnection() {

		final String userId = "p9916634"; // Retrieve the userId in a secure way
		final String password = "Psico1982"; // Retrieve the password in a
													// secure way
		String repoUri = "https://svuxprtc1.gvt.net.br:9443/ccm";
		ITeamRepository repo = null;

		try {
			TeamPlatform.startup();

			repo = TeamPlatform.getTeamRepositoryService().getTeamRepository(
					repoUri);
			repo.registerLoginHandler(new LoginHandler(userId, password));

			/* Do all of my work with myserver here. */

			repo.login(new SysoutProgressMonitor());

		} catch (TeamRepositoryException e) {
			TeamPlatform.shutdown();
			System.out.println(e.getStackTrace());
		}

		return repo;
	}

	public void closeConn() throws TeamRepositoryException {
		TeamPlatform.shutdown();
	}
}