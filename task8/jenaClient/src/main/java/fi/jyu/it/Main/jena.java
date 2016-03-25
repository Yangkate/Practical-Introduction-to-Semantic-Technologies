/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.jyu.it.Main;



import java.io.IOException;
import java.util.List;


import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;



import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.DatasetFactory;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.reasoner.Reasoner;
import com.hp.hpl.jena.reasoner.rulesys.GenericRuleReasoner;
import com.hp.hpl.jena.reasoner.rulesys.Rule;

/**
 *
 * @author Administrator
 */
public class jena extends HttpServlet {

	public jena() {
		super();
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		String url = req.getParameter("url");
		String rule = req.getParameter("ruleform");
		String query = req.getParameter("queryform");
		Dataset dataset = null;
		System.out.println(url);
		
//		StatementSink sink = null;
//		XMLReader reader = null;
//		InputSource source = null;
//		try {
//			reader = ParserFactory.createReaderForFormat(sink, Format.XHTML);
//		} catch (SAXException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		} // or HTML, still an XMLReader
//		
//		try {
//			reader.parse(source);
//		} catch (SAXException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		} // Your sink will be sent triples
//		try {
//			Class.forName("net.rootdev.javardfa.jena.RDFaReader");
//		} catch (ClassNotFoundException e) {
//			e.printStackTrace();
//		}
		
//		Model m = ModelFactory.createDefaultModel();
//        StatementSink sink = new JenaStatementSink(m);
//        XMLReader parser = ParserFactory.createReaderForFormat(sink, Format.HTML);
//        parser.parse(this.getClass().getResource(url).toExternalForm());
//        Resource r = m.listSubjects().nextResource();
//        
        //assertTrue("HTML 5 base correct", r.getURI().endsWith("simple.html"));
        
		if (rule.contains("http://users.jyu.fi/~jiayang/compound.rdf")) {
			try {
				Class.forName("net.rootdev.javardfa.RDFaReader");
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Model model = ModelFactory.createDefaultModel();
		model.read(url, "N3");//to read a html with "HTML" , a xml with "XHTML"
		Reasoner reasoner = new GenericRuleReasoner(Rule.parseRules(rule));

		        reasoner.setDerivationLogging(true);
			InfModel inf = ModelFactory.createInfModel(reasoner,
					model);
			dataset = DatasetFactory.create(inf);
			System.out.print("rule is not null");
		} else {
			Model model = ModelFactory.createDefaultModel();
			model.read(url, "N3");
			model.write(System.out, "N3");
			dataset = DatasetFactory.create(model);
		}
		Query q = QueryFactory.create(query);

		// ResultSetFormatter.out(System.out, resultSet,q);
		if (query.contains("ASK")) {
			QueryExecution qexec = QueryExecutionFactory.create(q, dataset);
			boolean result = qexec.execAsk();
			System.out.print(result);
			resp.setCharacterEncoding("UTF-8"); 
			resp.getWriter().println("---URL---");
			resp.getWriter().println(url);
			resp.getWriter().println("---Rule---");
			resp.getWriter().println(rule);
			resp.getWriter().println("---Query---");
			resp.getWriter().println(query);
			resp.getWriter().println("---Result---");
			resp.getWriter().print(result);	
			qexec.close();
		}
		if (query.contains("SELECT")) {
			QueryExecution qexec = QueryExecutionFactory.create(q, dataset);
			//Set<RDFNode> results = new HashSet<RDFNode>();
			ResultSet resultSet = qexec.execSelect();
			List<String> varNames = resultSet.getResultVars();
			for (int j = 0; j < varNames.size(); j++) {
				resp.setCharacterEncoding("UTF-8"); 
				resp.getWriter().println("---URL---");
				resp.getWriter().println(url);
				resp.getWriter().println("---Rule---");
				resp.getWriter().println(rule);
				resp.getWriter().println("---Query---");
				resp.getWriter().println(query);
				resp.getWriter().println("---Result---");
				resp.getWriter().print(varNames.get(j).toString().toUpperCase()+"                                     ");}
				resp.getWriter().println();
			while (resultSet.hasNext()) {
				QuerySolution row = (QuerySolution) resultSet.next();
				for(String varName : varNames) {
					RDFNode value = row.get(varName);
					System.out.print(value);
					resp.setCharacterEncoding("UTF-8"); 
					resp.getWriter().print(value+" ");			
//					if(value != null) {
//						results.add(value);
//					}
				}
				resp.getWriter().println();
			}
			qexec.close();
		}
		if (query.contains("CONSTRUCT") || query.contains("DESCRIBE")) {
			QueryExecution qexec = QueryExecutionFactory.create(q, dataset);
			Model resultModel = qexec.execConstruct();
			
		resp.setCharacterEncoding("UTF-8"); 
		resp.getWriter().println("---URL---");
		resp.getWriter().println(url);
		resp.getWriter().println("---Rule---");
		resp.getWriter().println(rule); 
		resp.getWriter().println("---Query---");
		resp.getWriter().println(query);
		resp.getWriter().println("---Result---");
			resp.getWriter().print("subject" + "  " + "predicate" + "   " 
			+"object"+"\n");
	
	
		resp.getWriter().println(resultModel.write(System.out, "TURTLE"));
			
			qexec.close();
		}

	}
}
