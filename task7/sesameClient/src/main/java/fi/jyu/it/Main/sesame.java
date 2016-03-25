/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.jyu.it.Main;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.Value;
import org.openrdf.query.Binding;
import org.openrdf.query.BindingSet;
import org.openrdf.query.BooleanQuery;
import org.openrdf.query.GraphQuery;
import org.openrdf.query.GraphQueryResult;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.Query;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.http.HTTPRepository;

/**
 *
 * @author Administrator
 */
public class sesame extends HttpServlet {

	public sesame() {
		super();
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {

		String url = req.getParameter("comment");
		System.out.println(url);
		String sesameServer = "http://localhost:8080/openrdf-sesame";
		String repositoryID = "ties452";
		Repository repo = new HTTPRepository(sesameServer, repositoryID);
		try {
			repo.initialize();
		} catch (RepositoryException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		RepositoryConnection con = null;
		try {
			con = repo.getConnection();
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String queryString = url;
		Query query = null;
		try {
			query = con.prepareQuery(QueryLanguage.SPARQL, queryString);
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedQueryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// ASK Query
		if (query instanceof BooleanQuery) {
			boolean result = ((BooleanQuery) query).evaluate();

			System.out.println(result);
			resp.setCharacterEncoding("UTF-8"); 
			resp.getWriter().println("---Query---");
			resp.getWriter().println(query);
			resp.getWriter().println("---Result---");
			resp.getWriter().println(result);
		}

		// SELECT Query
		if (query instanceof TupleQuery) {
			TupleQueryResult resultT = ((TupleQuery) query).evaluate();
			List<String> bindingNames = resultT.getBindingNames();
			int size = bindingNames.size();
			Value[] Values = new Value[size];
			for (int j = 0; j < size; j++) {
			resp.setCharacterEncoding("UTF-8"); 
			resp.getWriter().println("---Query---");
			resp.getWriter().println(query);
			resp.getWriter().println("---Result---");
			resp.getWriter().print(bindingNames.get(j).toString().toUpperCase()+"                                     ");}
			resp.getWriter().println();
			
			while (resultT.hasNext()) {
				BindingSet bindingSet = resultT.next();
				
				for (int i = 0; i < size; i++) {
					Values[i] = bindingSet.getValue(bindingNames.get(i));
					System.out.println(Values[i]);
					resp.setCharacterEncoding("UTF-8"); 
					resp.getWriter().print(Values[i]);
					
				}
				
				resp.getWriter().println();
			}
			
			resultT.close();
		}
			// CONSTRUCT Query
			if (query instanceof GraphQuery) {
				GraphQueryResult resultG = ((GraphQuery) query).evaluate();
				resp.setCharacterEncoding("UTF-8"); 
				resp.getWriter().println("---Query---");
				resp.getWriter().println(query);
				resp.getWriter().println("---Result---");
				resp.getWriter().print("subject" + "                                          " + "predicate" + "                                       " 
				+"object");
				resp.getWriter().println();
				while (resultG.hasNext()) {
					Statement st = resultG.next();
					String subject = st.getSubject().toString();
					String predicate = st.getPredicate().toString();
					String object=st.getObject().toString();
					System.out.println(subject + " " + predicate + " " + object);
					resp.setCharacterEncoding("UTF-8"); 
					resp.getWriter().print(subject + "    " + predicate + "   " + object);
					resp.getWriter().println();
				}
                
				// Do not forget!
				resultG.close();
			}
		
	}
}
