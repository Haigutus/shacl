package org.topbraid.shacl.model;

import org.apache.jena.enhanced.BuiltinPersonalities;
import org.apache.jena.enhanced.Personality;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.sparql.function.FunctionRegistry;
import org.apache.jena.sparql.pfunction.PropertyFunctionRegistry;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.topbraid.shacl.arq.functions.HasShapeFunction;
import org.topbraid.shacl.arq.functions.IsValidForDatatypeFunction;
import org.topbraid.shacl.arq.functions.TargetContainsPFunction;
import org.topbraid.shacl.js.SHJS;
import org.topbraid.shacl.model.impl.SHConstraintComponentImpl;
import org.topbraid.shacl.model.impl.SHJSConstraintImpl;
import org.topbraid.shacl.model.impl.SHJSExecutableImpl;
import org.topbraid.shacl.model.impl.SHJSFunctionImpl;
import org.topbraid.shacl.model.impl.SHNodeShapeImpl;
import org.topbraid.shacl.model.impl.SHParameterImpl;
import org.topbraid.shacl.model.impl.SHParameterizableImpl;
import org.topbraid.shacl.model.impl.SHParameterizableInstanceImpl;
import org.topbraid.shacl.model.impl.SHParameterizableTargetImpl;
import org.topbraid.shacl.model.impl.SHPropertyShapeImpl;
import org.topbraid.shacl.model.impl.SHResultImpl;
import org.topbraid.shacl.model.impl.SHSPARQLConstraintImpl;
import org.topbraid.shacl.model.impl.SHSPARQLFunctionImpl;
import org.topbraid.shacl.model.impl.SHSPARQLTargetImpl;
import org.topbraid.shacl.util.SHACLUtil;
import org.topbraid.shacl.vocabulary.SH;
import org.topbraid.shacl.vocabulary.TOSH;
import org.topbraid.spin.util.JenaUtil;
import org.topbraid.spin.util.SimpleImplementation;

public class SHFactory {
    
    static {
		init(BuiltinPersonalities.model);
    }

    
	private static void init(Personality<RDFNode> p) {
		p.add(SHConstraintComponent.class, new SimpleImplementation(SH.ConstraintComponent.asNode(), SHConstraintComponentImpl.class));
		p.add(SHJSConstraint.class, new SimpleImplementation(SHJS.JSConstraint.asNode(), SHJSConstraintImpl.class));
		p.add(SHJSExecutable.class, new SimpleImplementation(SHJS.JSExecutable.asNode(), SHJSExecutableImpl.class));
		p.add(SHJSFunction.class, new SimpleImplementation(SHJS.JSFunction.asNode(), SHJSFunctionImpl.class));
    	p.add(SHParameter.class, new SimpleImplementation(SH.Parameter.asNode(), SHParameterImpl.class));
    	p.add(SHParameterizable.class, new SimpleImplementation(SH.Parameterizable.asNode(), SHParameterizableImpl.class));
    	p.add(SHParameterizableInstance.class, new SimpleImplementation(RDFS.Resource.asNode(), SHParameterizableInstanceImpl.class));
    	p.add(SHParameterizableTarget.class, new SimpleImplementation(SH.Target.asNode(), SHParameterizableTargetImpl.class));
    	p.add(SHPropertyShape.class, new SimpleImplementation(SH.PropertyShape.asNode(), SHPropertyShapeImpl.class));
    	p.add(SHResult.class, new SimpleImplementation(SH.AbstractResult.asNode(), SHResultImpl.class));
    	p.add(SHNodeShape.class, new SimpleImplementation(SH.NodeShape.asNode(), SHNodeShapeImpl.class));
		p.add(SHSPARQLConstraint.class, new SimpleImplementation(SH.SPARQLConstraint.asNode(), SHSPARQLConstraintImpl.class));
		p.add(SHSPARQLFunction.class, new SimpleImplementation(SH.SPARQLFunction.asNode(), SHSPARQLFunctionImpl.class));
		p.add(SHSPARQLTarget.class, new SimpleImplementation(SH.SPARQLTarget.asNode(), SHSPARQLTargetImpl.class));

		FunctionRegistry.get().put(TOSH.hasShape.getURI(), HasShapeFunction.class);
		FunctionRegistry.get().put("http://spinrdf.org/spif#isValidForDatatype", IsValidForDatatypeFunction.class);
		PropertyFunctionRegistry.get().put(TargetContainsPFunction.URI, TargetContainsPFunction.class);
    }
	
	
	public static SHConstraintComponent asConstraintComponent(RDFNode resource) {
		return resource.as(SHConstraintComponent.class);
	}
	
	
	public static SHSPARQLFunction asSPARQLFunction(RDFNode resource) {
		return resource.as(SHSPARQLFunction.class);
	}
	
	
	public static SHParameter asParameter(RDFNode resource) {
		return resource.as(SHParameter.class);
	}
	
	
	public static SHParameterizable asParameterizable(RDFNode resource) {
		return resource.as(SHParameterizable.class);
	}
	
	
	public static SHPropertyShape asPropertyConstraint(RDFNode node) {
		return node.as(SHPropertyShape.class);
	}
	
	
	public static SHNodeShape asNodeShape(RDFNode node) {
		return node.as(SHNodeShape.class);
	}
	
	
	public static SHSPARQLConstraint asSPARQLConstraint(RDFNode node) {
		return node.as(SHSPARQLConstraint.class);
	}
	
	
	public static SHSPARQLTarget asSPARQLTarget(RDFNode node) {
		return node.as(SHSPARQLTarget.class);
	}
	
	
	public static SHParameterizableInstance asTemplateCall(RDFNode resource) {
		return resource.as(SHParameterizableInstance.class);
	}
	
	
	public static SHShape asShape(RDFNode node) {
		if(node instanceof Resource && isPropertyShape((Resource)node)) {
			return asPropertyConstraint(node);
		}
		else if(node instanceof Resource && isParameter((Resource)node)) {
			return asParameter(node);
		}
		else {
			return asNodeShape(node);
		}
	}
	
	
	public static SHParameterizableTarget asParameterizableTarget(RDFNode node) {
		return node.as(SHParameterizableTarget.class);
	}

	
	public static boolean isSPARQLConstraint(RDFNode node) {
		return node instanceof Resource && 
				(JenaUtil.hasIndirectType((Resource)node, SH.SPARQLConstraint) ||
				(!((Resource)node).hasProperty(RDF.type) && node.getModel().contains(null, SH.sparql, node)));
	}
	
	
	public static boolean isSPARQLTarget(RDFNode node) {
		return node instanceof Resource && JenaUtil.hasIndirectType((Resource)node, SH.SPARQLTarget);
	}
	
	
	/**
	 * Checks if a given node is a Shape.  Note this is just an approximation based
	 * on a couple of hard-coded properties.
	 * @param node  the node to test
	 * @return true if node is a Shape
	 */
	public static boolean isShape(RDFNode node) {
		if(node instanceof Resource) {
			if(JenaUtil.hasIndirectType((Resource)node, SH.Shape)) {
				return true;
			}
			else if(node.isAnon() && !((Resource)node).hasProperty(RDF.type)) {
				// TODO: This logic is not really correct - it should also test that if
				//       other rdf:type triples are present
				if(node.getModel().contains(null, SH.node, node)) {
					return true;
				}
			}
		}
		return false;
	}
	
	
	public static boolean isParameterizableConstraint(RDFNode node) {
		if(node instanceof Resource) {
			Resource r = (Resource) node;
			if(!r.hasProperty(RDF.type)) {
				return  node.getModel().contains(null, SH.property, node) ||
						node.getModel().contains(null, SH.parameter, node);
			}
			else if(r.hasProperty(RDF.type, SH.NodeShape) ||
					r.hasProperty(RDF.type, SH.PropertyShape) ||
					r.hasProperty(RDF.type, SH.Parameter)) {
				return true;
			}
		}
		return false;
	}
	
	
	public static boolean isParameter(Resource resource) {
		return resource.hasProperty(RDF.type, SH.Parameter) ||
				(!resource.hasProperty(RDF.type) && resource.getModel().contains(null, SH.parameter, resource));
	}
    
    
	/**
	 * Checks if a given RDFNode represents a parameterizable instance.
	 * It either needs to be an instance of an instance of sh:Parameterizable,
	 * or be a typeless node that has an incoming edge via a property 
	 * that has a declared sh:defaultType, such as sh:property.
	 * @param node  the node to check
	 * @return true if node is a parameterizable instance
	 */
	public static boolean isParameterizableInstance(RDFNode node) {
		if(node instanceof Resource) {
			Resource resource = (Resource) node;
			
			// Return true if this has sh:Parameterizable as its metaclass
			for(Resource type : JenaUtil.getTypes(resource)) {
				if(JenaUtil.hasIndirectType(type, SH.Parameterizable)) {
					return true;
				}
			}
			
			// If this is a typeless node, check for defaultType of incoming references
			if(!resource.hasProperty(RDF.type)) {
				Resource dt = SHACLUtil.getResourceDefaultType(resource);
				if(dt != null && JenaUtil.hasIndirectType(dt, SH.Parameterizable)) {
					return true;
				}
			}
		}
		return false;
	}
	
	
	public static boolean isPropertyShape(Resource resource) {
		return resource.hasProperty(RDF.type, SH.PropertyShape) ||
				resource.getModel().contains(null, SH.property, resource);
	}
}