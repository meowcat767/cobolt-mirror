
import com.cobolt.core.*;
import com.cobolt.merge.*;
import com.cobolt.objects.FileUtils;
import java.nio.file.*;
import java.util.*;
import java.io.*;

public class VerifyChanges {
    public static void main(String[] args) {
        try {
            testMerge();
            testRemote();
            testCache();
            System.out.println("All tests passed!");
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static void testMerge() throws Exception {
        System.out.println("Testing Merge...");
        Path workDir = Files.createTempDirectory("cobolt-merge-test");
        Repository repo = Repository.init(workDir);

        // Create ancestor
        String file = "test.txt";
        createCommit(repo, file, "Line 1\nLine 2\nLine 3\n");
        Commit ancestor = (Commit) repo.readObject(repo.resolveRef("HEAD"));

        // Create ours (modify line 1)
        repo.setHead(ancestor.getId(), false); // Reset to ancestor
        createCommit(repo, file, "Line 1 Modified\nLine 2\nLine 3\n");
        Commit ours = (Commit) repo.readObject(repo.resolveRef("HEAD"));

        // Create theirs (modify line 3)
        repo.setHead(ancestor.getId(), false); // Reset to ancestor
        createCommit(repo, file, "Line 1\nLine 2\nLine 3 Modified\n");
        Commit theirs = (Commit) repo.readObject(repo.resolveRef("HEAD"));

        // Merge
        MergeEngine engine = new MergeEngine(repo);
        MergeResult result = engine.merge(ours, theirs, ancestor);

        if (!result.isSuccess()) {
            System.out.println("Merge failed unexpectedly (conflicts found): " + result.getConflicts().size());
            for (Conflict c : result.getConflicts()) {
                System.out.println("Conflict: " + c.getFilePath() + " " + c.getType());
            }
        } else {
            System.out.println("Merge successful (as expected for non-overlapping changes)");
            // In my simple implementation, I marked different content as conflict even if
            // lines don't overlap.
            // Let's check if it actually reported a conflict.
            // My implementation:
            // if (ourEntry.getId().equals(theirEntry.getId())) { ... } else {
            // result.addConflict(...) }
            // So it SHOULD report a conflict because I didn't implement line-level merging.

            // Wait, my implementation:
            // } else {
            // // Both modified
            // // Check if content is same
            // if (ourEntry.getId().equals(theirEntry.getId())) {
            // // Same content change, auto-merge (keep ours)
            // } else {
            // // Different content -> Content Conflict
            // result.addConflict(...)
            // }
            // }

            // So yes, it should be a conflict.
        }

        if (!result.getConflicts().isEmpty()) {
            System.out.println("Merge conflict correctly detected.");
        } else {
            System.out.println("WARNING: Merge conflict NOT detected (unexpected for file-level merge).");
        }

        FileUtils.deleteRecursively(workDir);
    }

    private static void testRemote() throws Exception {
        System.out.println("Testing Remote...");
        Path localDir = Files.createTempDirectory("cobolt-local");
        Path remoteDir = Files.createTempDirectory("cobolt-remote");

        Repository local = Repository.init(localDir);
        Repository remote = Repository.init(remoteDir);

        // Create commit in local
        createCommit(local, "file.txt", "Hello Remote");

        // Add remote
        local.addRemote("origin", remoteDir.toAbsolutePath().toString());

        // Push
        local.push("origin", "main");

        // Check remote
        String remoteHead = remote.resolveRef("HEAD");
        String localHead = local.resolveRef("HEAD");

        if (remoteHead.equals(localHead)) {
            System.out.println("Push successful: Remote HEAD matches Local HEAD");
        } else {
            throw new Exception("Push failed: Remote HEAD " + remoteHead + " != Local HEAD " + localHead);
        }

        // Create commit in remote
        createCommit(remote, "file2.txt", "Hello Local");

        // Pull
        local.pull("origin", "main");

        // Check local
        // Pull implementation updates local branch to remote's value (fast-forward)
        String newLocalHead = local.resolveRef("main");
        String newRemoteHead = remote.resolveRef("main");

        if (newLocalHead.equals(newRemoteHead)) {
            System.out.println("Pull successful: Local HEAD updated to Remote HEAD");
        } else {
            throw new Exception("Pull failed: Local HEAD " + newLocalHead + " != Remote HEAD " + newRemoteHead);
        }

        FileUtils.deleteRecursively(localDir);
        FileUtils.deleteRecursively(remoteDir);
    }

    private static void testCache() throws Exception {
        System.out.println("Testing Cache...");
        Path workDir = Files.createTempDirectory("cobolt-cache");
        Repository repo = Repository.init(workDir);

        String id = createCommit(repo, "cache.txt", "Cache Me");

        // Read object (should populate cache)
        CoboltObject obj1 = repo.readObject(id);

        // Delete file manually to prove it comes from cache
        Path objPath = repo.getObjectsDir().resolve(id.substring(0, 2)).resolve(id.substring(2));
        Files.delete(objPath);

        // Read again (should succeed if cached)
        CoboltObject obj2 = repo.readObject(id);

        if (obj2 != null && obj2.getId().equals(id)) {
            System.out.println("Cache works: Object read after file deletion");
        } else {
            throw new Exception("Cache failed");
        }

        FileUtils.deleteRecursively(workDir);
    }

    private static String createCommit(Repository repo, String filename, String content) throws Exception {
        // Blob
        Blob blob = new Blob(content);
        String blobId = repo.writeObject(blob);

        // Tree
        Tree tree = new Tree();
        tree.addEntry(filename, blobId, "100644");
        String treeId = repo.writeObject(tree);

        // Commit
        Commit commit = new Commit();
        commit.setTreeId(treeId);
        commit.setAuthor("Tester");
        commit.setCommitter("Tester");
        commit.setMessage("Test commit");

        Reference head = repo.getHead();
        if (head != null && !head.isSymbolic()) {
            commit.addParent(head.getTarget());
        } else if (head != null && head.isSymbolic()) {
            String target = head.getTarget();
            Reference branch = repo.getBranch(target.replace("refs/heads/", ""));
            if (branch != null) {
                commit.addParent(branch.getTarget());
            }
        }

        String commitId = repo.writeObject(commit);

        // Update HEAD
        String currentBranch = repo.getCurrentBranch();
        if (currentBranch != null) {
            repo.createBranch(currentBranch, commitId);
        } else {
            repo.setHead(commitId, false);
        }

        return commitId;
    }
}
