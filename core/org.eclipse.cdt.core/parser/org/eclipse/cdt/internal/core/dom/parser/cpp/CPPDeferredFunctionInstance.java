/*******************************************************************************
 * Copyright (c) 2005, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Markus Schorn (Wind River Systems)
 *******************************************************************************/
package org.eclipse.cdt.internal.core.dom.parser.cpp;

import org.eclipse.cdt.core.dom.ast.DOMException;
import org.eclipse.cdt.core.dom.ast.IASTParameterDeclaration;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.IFunction;
import org.eclipse.cdt.core.dom.ast.IFunctionType;
import org.eclipse.cdt.core.dom.ast.IParameter;
import org.eclipse.cdt.core.dom.ast.IScope;
import org.eclipse.cdt.core.dom.ast.IType;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPDeferredTemplateInstance;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPFunction;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPFunctionTemplate;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPParameter;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPTemplateDefinition;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPTemplateParameter;
import org.eclipse.cdt.core.parser.util.ObjectMap;
import org.eclipse.cdt.internal.core.dom.parser.ASTInternal;
import org.eclipse.cdt.internal.core.dom.parser.cpp.semantics.CPPTemplates;
import org.eclipse.cdt.internal.core.dom.parser.cpp.semantics.CPPVisitor;

/**
 * @author aniefer
 */
public class CPPDeferredFunctionInstance extends CPPUnknownBinding implements ICPPFunction, ICPPInternalFunction, ICPPDeferredTemplateInstance {
	private IType[] fArguments;
	private ICPPFunctionTemplate fFunctionTemplate;

	private ObjectMap fArgmap;
	private IParameter [] fParameters;
	private IFunctionType fFunctionType;

	public CPPDeferredFunctionInstance( ICPPFunctionTemplate template, IType[] arguments ) throws DOMException {
		super(template.getOwner(), new CPPASTName(template.getNameCharArray()));
		fArguments= arguments;
		fFunctionTemplate= template;
	}

	public ICPPTemplateDefinition getTemplateDefinition() {
		return fFunctionTemplate;
	}

	public IBinding getSpecializedBinding() {
		return fFunctionTemplate;
	}

	public ObjectMap getArgumentMap() {
		if (fArgmap == null) {
			fArgmap= ObjectMap.EMPTY_MAP;
			try {
				ICPPTemplateParameter[] params= fFunctionTemplate.getTemplateParameters();
				ObjectMap result= new ObjectMap(params.length);
				for (int i=0; i < params.length; i++) {
					if (i < fArguments.length) {
						result.put(params[i], fArguments[i]);
					}
				}
				fArgmap= result;
			} catch (DOMException e) {
			}
		}
		return fArgmap;
	}

	public IType[] getArguments() {
		return fArguments;
	}
	
	public IParameter[] getParameters() throws DOMException {
		ObjectMap map= getArgumentMap();
		if (map == null || map.isEmpty()) {
			return ((ICPPFunction)getTemplateDefinition()).getParameters();
		}
		if( fParameters == null ){
			IParameter [] params = ((ICPPFunction)getTemplateDefinition()).getParameters();
			fParameters = new IParameter[ params.length ];
			for (int i = 0; i < params.length; i++) {
				fParameters[i] = new CPPParameterSpecialization( (ICPPParameter)params[i], null, getArgumentMap() );
			}
		}
		return fParameters;
	}

	public IScope getFunctionScope() {
		return null;
	}
	
	public IFunctionType getType() throws DOMException {
		if( fFunctionType == null ){
            IFunctionType ft = ((ICPPFunction)getTemplateDefinition()).getType(); 
            IType returnType = ft.getReturnType();
            // mstodo- is that necessary?
			returnType = CPPTemplates.instantiateType( returnType, getArgumentMap(), null);
			fFunctionType = CPPVisitor.createImplicitFunctionType( returnType, getParameters(), null);
        }
        
        return fFunctionType;
	}


	public boolean isStatic() throws DOMException {
		return ((ICPPFunction)getTemplateDefinition()).isStatic();
	}

	public boolean isMutable() throws DOMException {
		return ((ICPPFunction)getTemplateDefinition()).isMutable();
	}

	public boolean isInline() throws DOMException {
		return ((ICPPFunction)getTemplateDefinition()).isInline();	
	}

	public boolean isExternC() throws DOMException {
		return ((ICPPFunction)getTemplateDefinition()).isExternC();	
	}

	public boolean isExtern() throws DOMException {
		return ((ICPPFunction)getTemplateDefinition()).isExtern();
	}

	public boolean isAuto() throws DOMException {
		return ((ICPPFunction)getTemplateDefinition()).isAuto();
	}

	public boolean isRegister() throws DOMException {
		return ((ICPPFunction)getTemplateDefinition()).isRegister();
	}

	public boolean takesVarArgs() throws DOMException {
		return ((ICPPFunction)getTemplateDefinition()).takesVarArgs();
	}

    public boolean isStatic( boolean resolveAll) {
    	try {
			return ASTInternal.isStatic((IFunction) getTemplateDefinition(), resolveAll);
		} catch (DOMException e) {
			return false;
		}
    }

    public IBinding resolveParameter(IASTParameterDeclaration param) {
        return null;
    }
}
