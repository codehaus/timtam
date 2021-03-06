// generated by Glue Standard 5.0.1 (wsdl2java) on Fri Aug 27 12:11:21 BST 2004
package com.atlassian.confluence.remote;

public interface IConfluenceSoapService
  {
  String[] getPermissions( String arg0, String arg1 ) throws com.atlassian.confluence.remote.InvalidSessionException,com.atlassian.confluence.remote.RemoteException;
  RemoteSearchResult[] search( String arg0, String arg1, int arg2 ) throws com.atlassian.confluence.remote.InvalidSessionException,com.atlassian.confluence.remote.RemoteException;
  RemoteUser getUser( String arg0, String arg1 ) throws com.atlassian.confluence.remote.InvalidSessionException,com.atlassian.confluence.remote.RemoteException;
  String login( String arg0, String arg1 ) throws com.atlassian.confluence.remote.AuthenticationFailedException,com.atlassian.confluence.remote.RemoteException;
  RemoteServerInfo getServerInfo( String arg0 );
  void addUser( String arg0, RemoteUser arg1, String arg2 ) throws com.atlassian.confluence.remote.NotPermittedException,com.atlassian.confluence.remote.InvalidSessionException,com.atlassian.confluence.remote.RemoteException;
  boolean addGroup( String arg0, String arg1 ) throws com.atlassian.confluence.remote.NotPermittedException,com.atlassian.confluence.remote.InvalidSessionException,com.atlassian.confluence.remote.RemoteException;
  RemoteSpace getSpace( String arg0, String arg1 ) throws com.atlassian.confluence.remote.InvalidSessionException,com.atlassian.confluence.remote.RemoteException;
  RemotePage getPage( String arg0, long arg1 ) throws com.atlassian.confluence.remote.InvalidSessionException,com.atlassian.confluence.remote.RemoteException;
  RemoteLock[] getLocks( String arg0, long arg1 ) throws com.atlassian.confluence.remote.InvalidSessionException,com.atlassian.confluence.remote.RemoteException;
  RemoteAttachment[] getAttachments( String arg0, long arg1 ) throws com.atlassian.confluence.remote.InvalidSessionException,com.atlassian.confluence.remote.RemoteException;
  RemoteComment[] getComments( String arg0, long arg1 ) throws com.atlassian.confluence.remote.InvalidSessionException,com.atlassian.confluence.remote.RemoteException;
  RemoteSpaceSummary[] getSpaces( String arg0 ) throws com.atlassian.confluence.remote.InvalidSessionException,com.atlassian.confluence.remote.RemoteException;
  RemotePageSummary[] getPages( String arg0, String arg1 ) throws com.atlassian.confluence.remote.InvalidSessionException,com.atlassian.confluence.remote.RemoteException;
  boolean logout( String arg0 ) throws com.atlassian.confluence.remote.RemoteException;
  RemoteBlogEntrySummary[] getBlogEntries( String arg0, String arg1 ) throws com.atlassian.confluence.remote.InvalidSessionException,com.atlassian.confluence.remote.RemoteException;
  RemoteBlogEntry getBlogEntry( String arg0, long arg1 );
  RemotePageHistory[] getPageHistory( String arg0, long arg1 ) throws com.atlassian.confluence.remote.InvalidSessionException,com.atlassian.confluence.remote.RemoteException;
  String renderContent( String arg0, String arg1, long arg2, String arg3 ) throws com.atlassian.confluence.remote.InvalidSessionException,com.atlassian.confluence.remote.RemoteException;
  Boolean deletePage( String arg0, long arg1 ) throws com.atlassian.confluence.remote.NotPermittedException,com.atlassian.confluence.remote.InvalidSessionException,com.atlassian.confluence.remote.RemoteException;
  RemoteBlogEntry storeBlogEntry( String arg0, RemoteBlogEntry arg1 ) throws com.atlassian.confluence.remote.VersionMismatchException,com.atlassian.confluence.remote.NotPermittedException,com.atlassian.confluence.remote.InvalidSessionException,com.atlassian.confluence.remote.RemoteException;
  RemotePage storePage( String arg0, RemotePage arg1 ) throws com.atlassian.confluence.remote.VersionMismatchException,com.atlassian.confluence.remote.NotPermittedException,com.atlassian.confluence.remote.InvalidSessionException,com.atlassian.confluence.remote.RemoteException;
  String[] getUserGroups( String arg0, String arg1 ) throws com.atlassian.confluence.remote.NotPermittedException,com.atlassian.confluence.remote.InvalidSessionException,com.atlassian.confluence.remote.RemoteException;
  void addUserToGroup( String arg0, String arg1, String arg2 ) throws com.atlassian.confluence.remote.NotPermittedException,com.atlassian.confluence.remote.InvalidSessionException,com.atlassian.confluence.remote.RemoteException;
  RemoteSpace addSpace( String arg0, RemoteSpace arg1 ) throws com.atlassian.confluence.remote.NotPermittedException,com.atlassian.confluence.remote.InvalidSessionException,com.atlassian.confluence.remote.RemoteException;
  }
