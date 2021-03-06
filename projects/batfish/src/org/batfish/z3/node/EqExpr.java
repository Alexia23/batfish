package org.batfish.z3.node;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.batfish.z3.NodProgram;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Z3Exception;

public class EqExpr extends BooleanExpr implements ComplexExpr {

   private IntExpr _lhs;
   private IntExpr _rhs;
   private List<Expr> _subExpressions;

   public EqExpr() {
      init();
   }

   public EqExpr(IntExpr lhs, IntExpr rhs) {
      init();
      _lhs = lhs;
      _rhs = rhs;
      refreshSubExpressions();
   }

   @Override
   public List<Expr> getSubExpressions() {
      return _subExpressions;
   }

   @Override
   public Set<String> getVariables() {
      Set<String> variables = new HashSet<String>();
      variables.addAll(_lhs.getVariables());
      variables.addAll(_rhs.getVariables());
      return variables;
   }

   private void init() {
      _subExpressions = new ArrayList<Expr>();
      _printer = new CollapsedComplexExprPrinter(this);
   }

   private void refreshSubExpressions() {
      _subExpressions.clear();
      _subExpressions.add(new IdExpr("="));
      _subExpressions.add(_lhs);
      _subExpressions.add(_rhs);
   }

   public void setLhs(IntExpr lhs) {
      _lhs = lhs;
      refreshSubExpressions();
   }

   public void setRhs(IntExpr rhs) {
      _rhs = rhs;
      refreshSubExpressions();
   }

   @Override
   public BooleanExpr simplify() {
      if (_lhs.equals(_rhs)) {
         return TrueExpr.INSTANCE;
      }
      else if (_lhs instanceof LitIntExpr && _rhs instanceof LitIntExpr) {
         return FalseExpr.INSTANCE;
      }
      else {
         return this;
      }
   }

   @Override
   public BoolExpr toBoolExpr(NodProgram nodProgram) throws Z3Exception {
      Context ctx = nodProgram.getContext();
      com.microsoft.z3.Expr lhs = _lhs.toBitVecExpr(nodProgram);
      com.microsoft.z3.Expr rhs = _rhs.toBitVecExpr(nodProgram);
      BoolExpr result = ctx.mkEq(lhs, rhs);
      return result;
   }

}
